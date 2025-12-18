package com.example.phoebe.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Test-specific security configuration that overrides the main security settings.
 * This configuration is loaded via {@code @Import(TestSecurityConfig.class)} in tests
 * where authentication and authorization are not the primary focus.
 * It disables security by permitting all requests, simplifying test setup.
 */
@TestConfiguration
public class TestSecurityConfig {

    /**
     * Creates a security filter chain that allows all HTTP requests.
     * The {@code @Primary} annotation ensures this bean replaces the production
     * SecurityFilterChain bean during the test context initialization.
     */
    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(AbstractHttpConfigurer::disable)
            .build();
    }
}
