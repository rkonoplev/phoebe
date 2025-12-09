package com.example.phoebe.unit.config;

import com.example.phoebe.config.SecurityConfig;
import com.example.phoebe.security.DatabaseUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private DatabaseUserDetailsService userDetailsService;

    @Test
    void passwordEncoderShouldReturnBCryptEncoder() {
        SecurityConfig config = new SecurityConfig(userDetailsService);

        PasswordEncoder encoder = config.passwordEncoder();

        assertNotNull(encoder);
        assertTrue(encoder.encode("test").startsWith("$2a$"));
    }
}
