package com.example.phoebe.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Простейший интеграционный тест для проверки запуска контекста Spring.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
class SimpleIntegrationTest {

    @Test
    void contextLoads() {
        // Если контекст загружается без ошибок, тест проходит
        assertTrue(true);
    }
}