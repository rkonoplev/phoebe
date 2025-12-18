package com.example.phoebe.integration;

import com.example.phoebe.integration.config.IntegrationTestConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for all integration tests.
 *
 * Uses Testcontainers to start a MySQL database once per test suite.
 * Container lifecycle is fully managed by Testcontainers and JUnit.
 *
 *  Safe for Java 21
 *  Works locally and in CI
 *  No static initialization hacks
 */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@Import(IntegrationTestConfig.class)
public abstract class BaseIntegrationTest {

    /**
     * Shared MySQL container for all integration tests.
     *
     * Static + @Container ensures:
     * - container starts only once
     * - lifecycle is controlled by Testcontainers
     * - no JVM crashes during class loading
     */
    @Container
    protected static final MySQLContainer<?> MYSQL_CONTAINER =
            new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("phoebe_test")
                    .withUsername("test")
                    .withPassword("test")
                    .withCommand(
                            "--max_connections=1000",
                            "--innodb_buffer_pool_size=64M"
                    );

    /**
     * Dynamically injects container properties into Spring context.
     * Executed AFTER the container has started.
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
        registry.add(
                "spring.datasource.driver-class-name",
                () -> "com.mysql.cj.jdbc.Driver"
        );
    }
}