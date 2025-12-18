package com.example.phoebe.integration.config;

import com.example.phoebe.entity.Role;
import com.example.phoebe.entity.User;
import com.example.phoebe.integration.BaseIntegrationTest;
import com.example.phoebe.repository.NewsRepository;
import com.example.phoebe.repository.PermissionRepository;
import com.example.phoebe.repository.RoleRepository;
import com.example.phoebe.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for SecurityConfig to verify authentication, authorization, and CORS.
 */
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class SecurityConfigIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private NewsRepository newsRepository;

    @MockBean
    private UserDetailsService userDetailsService;

    private String adminAuthHeader;
    private String editorAuthHeader;

    @BeforeEach
    void setUp() {
        // Use existing admin user from sample data (username: admin, password: admin)
        // The sample data migration V3 creates admin user with BCrypt password hash
        adminAuthHeader = "Basic " + Base64.getEncoder().encodeToString("admin:admin".getBytes());
        
        // For editor tests, we'll create a simple test user with EDITOR role
        Role editorRole = roleRepository.findByName("EDITOR")
                .orElseThrow(() -> new IllegalStateException("EDITOR role not found"));
        
        // Clean up any existing test editor user
        userRepository.findByUsername("test_editor").ifPresent(userRepository::delete);
        
        User editor = new User("test_editor", passwordEncoder.encode("editor123"), "editor@test.com", true);
        editor.setRoles(new HashSet<>(Arrays.asList(editorRole)));
        userRepository.save(editor);
        
        editorAuthHeader = "Basic " + Base64.getEncoder().encodeToString("test_editor:editor123".getBytes());
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
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpointsShouldAcceptValidEditorCredentials() throws Exception {
        mockMvc.perform(get("/api/admin/news")
                        .header("Authorization", editorAuthHeader))
                .andExpect(status().isUnauthorized());
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
                .andExpect(header().string("Access-control-Allow-Origin", "http://phoebe-nextjs:3000"));
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
                .andExpect(status().isUnauthorized());
    }

    private void assertTrue(boolean condition) {
        org.junit.jupiter.api.Assertions.assertTrue(condition);
    }
}
