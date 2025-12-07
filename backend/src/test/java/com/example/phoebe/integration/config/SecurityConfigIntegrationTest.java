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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for SecurityConfig to verify authentication, authorization, and CORS.
 */
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
        roleRepository.deleteAll();

        Role adminRole = new Role("ADMIN", null);
        Role editorRole = new Role("EDITOR", null);
        roleRepository.save(adminRole);
        roleRepository.save(editorRole);

        User admin = new User("admin", passwordEncoder.encode("admin123"), "admin@test.com", true);
        admin.addRole(adminRole);
        userRepository.save(admin);

        User editor = new User("editor", passwordEncoder.encode("editor123"), "editor@test.com", true);
        editor.addRole(editorRole);
        userRepository.save(editor);

        adminAuthHeader = "Basic " + Base64.getEncoder().encodeToString("admin:admin123".getBytes());
        editorAuthHeader = "Basic " + Base64.getEncoder().encodeToString("editor:editor123".getBytes());
    }

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

    @Test
    void adminEndpointsShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/admin/news"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpointsShouldAcceptValidAdminCredentials() throws Exception {
        mockMvc.perform(get("/api/admin/news")
                        .header("Authorization", adminAuthHeader))
                .andExpect(status().isOk());
    }

    @Test
    void adminEndpointsShouldAcceptValidEditorCredentials() throws Exception {
        mockMvc.perform(get("/api/admin/news")
                        .header("Authorization", editorAuthHeader))
                .andExpect(status().isOk());
    }

    @Test
    void adminEndpointsShouldRejectInvalidCredentials() throws Exception {
        String invalidAuth = "Basic " + Base64.getEncoder().encodeToString("invalid:wrong".getBytes());
        
        mockMvc.perform(get("/api/admin/news")
                        .header("Authorization", invalidAuth))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void corsShouldAllowLocalhostOrigins() throws Exception {
        mockMvc.perform(options("/api/public/news")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().exists("Access-Control-Allow-Methods"));
    }

    @Test
    void corsShouldAllowDockerOrigins() throws Exception {
        mockMvc.perform(options("/api/public/news")
                        .header("Origin", "http://phoebe-nextjs:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://phoebe-nextjs:3000"));
    }

    @Test
    void passwordEncoderShouldUseBCrypt() {
        String encoded = passwordEncoder.encode("testpassword");
        
        assertTrue(encoded.startsWith("$2a$") || encoded.startsWith("$2b$"));
        assertTrue(passwordEncoder.matches("testpassword", encoded));
    }

    @Test
    void authEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/admin/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authEndpointShouldReturnUserInfoWithValidAuth() throws Exception {
        mockMvc.perform(get("/api/admin/auth/me")
                        .header("Authorization", adminAuthHeader))
                .andExpect(status().isOk());
    }

    private void assertTrue(boolean condition) {
        org.junit.jupiter.api.Assertions.assertTrue(condition);
    }
}
