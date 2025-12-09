package com.example.phoebe.unit.config;

import com.example.phoebe.config.SecurityProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityPropertiesTest {

    @Test
    void securityPropertiesShouldCreateWithAllFields() {
        SecurityProperties properties = new SecurityProperties(
                "secret-key",
                3600000L,
                true,
                new String[]{"http://localhost:3000"}
        );

        assertEquals("secret-key", properties.jwtSecret());
        assertEquals(3600000L, properties.jwtExpirationMs());
        assertTrue(properties.enableCsrf());
        assertArrayEquals(new String[]{"http://localhost:3000"}, properties.allowedOrigins());
    }

    @Test
    void securityPropertiesShouldHandleNullValues() {
        SecurityProperties properties = new SecurityProperties(
                null,
                0L,
                false,
                null
        );

        assertNull(properties.jwtSecret());
        assertEquals(0L, properties.jwtExpirationMs());
        assertFalse(properties.enableCsrf());
        assertNull(properties.allowedOrigins());
    }
}
