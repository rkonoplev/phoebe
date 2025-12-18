package com.example.phoebe.security;

import org.springframework.stereotype.Service;

@Service
public class TestSecurityService {

    @RequireAnyRole({"ADMIN", "EDITOR"})
    public String requiresAdminOrEditor() {
        return "Success";
    }

    @RequireAllRoles({"ADMIN", "EDITOR"}) // Both ADMIN and EDITOR (unlikely scenario)
    public String requiresAdminAndEditor() {
        return "Success";
    }

    @RequireRole("ADMIN") // Only ADMIN
    public String requiresAdmin() {
        return "Success";
    }

    public String noSecurity() {
        return "Success";
    }
}
