package com.example.phoebe.security;

import org.springframework.stereotype.Service;

/**
 * Test-only service used to verify custom security annotations and aspects.
 * Contains no business logic.
 */
@Service
public class TestSecurityService {

    /**
     * Test method requiring either ADMIN or EDITOR role.
     * @return "Success" if access is granted.
     */
    @RequireAnyRole({"ADMIN", "EDITOR"})
    public String requiresAdminOrEditor() {
        return "Success";
    }

    /**
     * Test method requiring both ADMIN and EDITOR roles (unlikely scenario).
     * @return "Success" if access is granted.
     */
    @RequireAllRoles({"ADMIN", "EDITOR"}) // Both ADMIN and EDITOR (unlikely scenario)
    public String requiresAdminAndEditor() {
        return "Success";
    }

    /**
     * Test method requiring only the ADMIN role.
     * @return "Success" if access is granted.
     */
    @RequireRole("ADMIN") // Only ADMIN
    public String requiresAdmin() {
        return "Success";
    }

    /**
     * Test method with no security constraints.
     * @return "Success" always.
     */
    public String noSecurity() {
        return "Success";
    }
}
