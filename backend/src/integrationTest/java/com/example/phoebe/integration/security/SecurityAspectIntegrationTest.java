package com.example.phoebe.integration.security;

import com.example.phoebe.entity.Role;
import com.example.phoebe.entity.User;
import com.example.phoebe.integration.BaseIntegrationTest;
import com.example.phoebe.repository.RoleRepository;
import com.example.phoebe.repository.UserRepository;
import com.example.phoebe.security.RoleSecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.stream.Collectors;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class SecurityAspectIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RoleSecurityService roleSecurityService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User adminUser;
    private User editorUser;

    @BeforeEach
    void setUp() {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));
        Role editorRole = roleRepository.findByName("EDITOR")
                .orElseThrow(() -> new IllegalStateException("EDITOR role not found"));

        String uniqueId = String.valueOf(System.currentTimeMillis());
        
        adminUser = new User("sec_admin_" + uniqueId, "pass", "sec_admin_" + uniqueId + "@test.com", true);
        adminUser.setRoles(Set.of(adminRole));
        userRepository.save(adminUser);

        editorUser = new User("sec_editor_" + uniqueId, "pass", "sec_editor_" + uniqueId + "@test.com", true);
        editorUser.setRoles(Set.of(editorRole));
        userRepository.save(editorUser);

        SecurityContextHolder.clearContext();
    }

    @Test
    void adminShouldBeAbleToCallAdminMethod() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(adminUser.getUsername(), "pass", 
                    adminUser.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                        .collect(Collectors.toList()))
        );
        assertDoesNotThrow(() -> roleSecurityService.adminOnlyMethod());
    }

    @Test
    void editorShouldBeDeniedFromAdminMethod() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(editorUser.getUsername(), "pass", 
                    editorUser.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                        .collect(Collectors.toList()))
        );
        assertThrows(AccessDeniedException.class, () -> roleSecurityService.adminOnlyMethod());
    }
}
