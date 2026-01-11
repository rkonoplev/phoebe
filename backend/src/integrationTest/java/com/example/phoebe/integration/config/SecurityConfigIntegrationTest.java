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
import org.springframework.test.annotation.DirtiesContext;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SecurityConfigIntegrationTest extends BaseIntegrationTest {

    private static final String PUBLIC_NEWS_ENDPOINT = "/api/public/news";
    private static final String ADMIN_NEWS_ENDPOINT = "/api/admin/news";
    private static final String AUTH_HEADER_NAME = "Authorization";

    // Credentials from V3__insert_sample_data.sql
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    // Editor is not in V3, so we create it manually
    private static final String EDITOR_USERNAME = "editor_integration";
    private static final String EDITOR_PASSWORD = "editor123_secure_for_tests";

    private static final String TEST_PASSWORD = "testpassword_for_encoder";

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
        // Only delete extra users, NOT the 'admin' from V3
        // But to keep tests isolated, we delete all and recreate minimal set
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Recreate roles (V3 inserts them, but we ensure consistency)
        Role adminRole = new Role("ADMIN", "Administrator role");
        Role editorRole = new Role("EDITOR", "Editor role");
        adminRole = roleRepository.save(adminRole);
        editorRole = roleRepository.save(editorRole);

        // Re-create 'admin' exactly as in V3
        User admin = new User(
                ADMIN_USERNAME,
                passwordEncoder.encode(ADMIN_PASSWORD),
                "admin@example.com",
                true
        );
        admin.setRoles(Set.of(adminRole));
        userRepository.save(admin);

        // Create editor manually (not in V3)
        User editor = new User(
                EDITOR_USERNAME,
                passwordEncoder.encode(EDITOR_PASSWORD),
                "editor@test.com",
                true
        );
        editor.setRoles(Set.of(editorRole));
        userRepository.save(editor);

        adminAuthHeader = basicAuth(ADMIN_USERNAME, ADMIN_PASSWORD);
        editorAuthHeader = basicAuth(EDITOR_USERNAME, EDITOR_PASSWORD);
    }

    /* ===================== Public endpoints ===================== */

    @Test
    void publicEndpointsShouldBeAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get(PUBLIC_NEWS_ENDPOINT))
                .andExpect(status().isOk());
    }

    @Test
    void actuatorHealthShouldBeAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void swaggerUiShouldBeAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isOk());
    }

    /* ===================== Authentication ===================== */

    @Test
    void adminEndpointsShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get(ADMIN_NEWS_ENDPOINT))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpointsShouldAcceptAdminRole() throws Exception {
        mockMvc.perform(get(ADMIN_NEWS_ENDPOINT)
                        .header(AUTH_HEADER_NAME, adminAuthHeader))
                .andExpect(status().isOk());
    }

    @Test
    void adminEndpointsShouldRejectEditorRole() throws Exception {
        mockMvc.perform(get(ADMIN_NEWS_ENDPOINT)
                        .header(AUTH_HEADER_NAME, editorAuthHeader))
                .andExpect(status().isForbidden());
    }

    /* ===================== Auth endpoint ===================== */

    @Test
    void authMeEndpointShouldReturnUserInfoForAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/admin/auth/me")
                        .header(AUTH_HEADER_NAME, adminAuthHeader))
                .andExpect(status().isOk());
    }

    /* ===================== CORS ===================== */

    @Test
    void corsShouldAllowLocalhostOrigin() throws Exception {
        mockMvc.perform(options(PUBLIC_NEWS_ENDPOINT)
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
        mockMvc.perform(options(PUBLIC_NEWS_ENDPOINT)
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
        String encoded = passwordEncoder.encode(TEST_PASSWORD);

        assertTrue(encoded.startsWith("$2a$") || encoded.startsWith("$2b$"));
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, encoded));
    }

    private String basicAuth(String username, String password) {
        String token = username + ":" + password;
        return "Basic " + Base64.getEncoder()
                .encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }
}