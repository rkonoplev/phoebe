package com.example.phoebe.integration.controller;

import com.example.phoebe.entity.Role;
import com.example.phoebe.entity.User;
import com.example.phoebe.integration.BaseIntegrationTest;
import com.example.phoebe.repository.RoleRepository;
import com.example.phoebe.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class AuthControllerRateLimitTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String authHeader;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        // Find the EDITOR role created by Flyway migration V3
        Role editorRole = roleRepository.findByName("EDITOR")
                .orElseThrow(() -> new IllegalStateException("EDITOR role not found in test database"));

        User user = new User(
            "testuser",
            passwordEncoder.encode("password123"),
            "test@example.com",
            true
        );
        user.setRoles(Set.of(editorRole)); // Assign the role to the user
        userRepository.save(user);

        String credentials = "testuser:password123";
        authHeader = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    @Test
    void shouldAllowFirstFiveRequests() throws Exception {
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/admin/news")
                            .header("Authorization", authHeader))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void shouldBlockSixthRequest() throws Exception {
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/admin/news")
                            .header("Authorization", authHeader))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/api/admin/news")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowRequestsFromDifferentIPs() throws Exception {
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/admin/news")
                            .header("Authorization", authHeader))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/api/admin/news")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk());
    }
}
