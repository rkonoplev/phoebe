package com.example.phoebe.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Минимальный интеграционный тест для проверки REST API.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
class MinimalApiIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void homeEndpointShouldRequireAuthentication() {
        ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);
        
        // Корневой эндпоинт требует аутентификации согласно SecurityConfig
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}