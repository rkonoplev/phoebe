package com.example.phoebe.integration;

import com.example.phoebe.integration.config.IntegrationTestConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base class for all integration tests.
 * Uses H2 in-memory database for fast and reliable testing.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@Import(IntegrationTestConfig.class)
public abstract class BaseIntegrationTest {
    // Base class for integration tests
}