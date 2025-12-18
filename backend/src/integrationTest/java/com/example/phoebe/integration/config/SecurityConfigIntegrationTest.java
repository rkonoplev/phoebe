package com.example.phoebe.integration.config;

import com.example.phoebe.entity.Role;
import com.example.phoebe.entity.User;
import com.example.phoebe.integration.BaseIntegrationTest;
import com.example.phoebe.repository.RoleRepository;
import com.example.phoebe.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for Security configuration:
 * - authentication
 * - authorization (roles)
 * - public endpoints
 * - CORS configuration
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminAuthHeader;
    private String editorAuthHeader;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));

        Role editorRole = roleRepository.findByName("EDITOR")
                .orElseThrow(() -> new IllegalStateException("EDITOR role not found"));

        User admin = new User(
                "admin_test",
                passwordEncoder.encode("admin123"),
                "admin@test.com",
                true
        );
        admin.setRoles(Set.of(adminRole));
        userRepository.save(admin);

        User editor = new User(
                "editor_test",
                passwordEncoder.encode("editor123"),
                "editor@test.com",
                true
        );
        editor.setRoles(Set.of(editorRole));
        userRepository.save(editor);

        adminAuthHeader = basicAuth("admin_test", "admin123");
        editorAuthHeader = basicAuth("editor_test", "editor123");
    }

    /* ===================== Public endpoints ===================== */

    @Test
    void publicEndpointsShouldBeAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/public/news"))
                .andExpect(status().isOk());
    }

    @Test
    void actuatorHealthShouldBeAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void swaggerUiShouldBeAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    /* ===================== Authentication ===================== */

    @Test
    void adminEndpointsShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/admin/news"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpointsShouldAcceptAdminRole() throws Exception {
        mockMvc.perform(get("/api/admin/news")
                        .header("Authorization", adminAuthHeader))
                .andExpect(status().isOk());
    }

    @Test
    void adminEndpointsShouldRejectEditorRole() throws Exception {
        mockMvc.perform(get("/api/admin/news")
                        .header("Authorization", editorAuthHeader))
                .andExpect(status().isForbidden());
    }

    /* ===================== Auth endpoint ===================== */

    @Test
    void authMeEndpointShouldReturnUserInfoForAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/admin/auth/me")
                        .header("Authorization", adminAuthHeader))
                .andExpect(status().isOk());
    }

    /* ===================== CORS ===================== */

    @Test
    void corsShouldAllowLocalhostOrigin() throws Exception {
        mockMvc.perform(options("/api/public/news")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Access-Control-Allow-Origin",
                        "http://localhost:3000"
                ));
    }

    @Test
    void corsShouldAllowDockerOrigin() throws Exception {
        mockMvc.perform(options("/api/public/news")
                        .header("Origin", "http://phoebe-nextjs:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Access-Control-Allow-Origin",
                        "http://phoebe-nextjs:3000"
                ));
    }

    /* ===================== Infrastructure ===================== */

    @Test
    void passwordEncoderShouldUseBCrypt() {
        String encoded = passwordEncoder.encode("testpassword");

        assertTrue(encoded.startsWith("$2a$") || encoded.startsWith("$2b$"));
        assertTrue(passwordEncoder.matches("testpassword", encoded));
    }

    private String basicAuth(String username, String password) {
        String token = username + ":" + password;
        return "Basic " + Base64.getEncoder()
                .encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }
}
