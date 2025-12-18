package com.example.phoebe.integration.security;

import com.example.phoebe.entity.Role;
import com.example.phoebe.integration.BaseIntegrationTest;
import com.example.phoebe.repository.RoleRepository;
import com.example.phoebe.security.RoleSecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Integration tests for method-level security enforced by Spring Security.
 *
 * Verifies that:
 *  - ADMIN role can access admin-only methods
 *  - EDITOR role is denied access
 */
class SecurityAspectIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RoleSecurityService roleSecurityService;

    @Autowired
    private RoleRepository roleRepository;

    private Set<SimpleGrantedAuthority> adminAuthorities;
    private Set<SimpleGrantedAuthority> editorAuthorities;

    @BeforeEach
    void setUp() {
        adminAuthorities = roleRepository.findByName("ADMIN")
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toSet());

        editorAuthorities = roleRepository.findByName("EDITOR")
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toSet());

        SecurityContextHolder.clearContext();
    }

    @Test
    void adminShouldBeAbleToCallAdminMethod() {
        authenticate("admin", adminAuthorities);

        assertDoesNotThrow(() -> roleSecurityService.adminOnlyMethod());
    }

    @Test
    void editorShouldBeDeniedFromAdminMethod() {
        authenticate("editor", editorAuthorities);

        assertThrows(AccessDeniedException.class,
                () -> roleSecurityService.adminOnlyMethod());
    }

    private void authenticate(String username, Set<SimpleGrantedAuthority> authorities) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, "N/A", authorities)
        );
    }
}
