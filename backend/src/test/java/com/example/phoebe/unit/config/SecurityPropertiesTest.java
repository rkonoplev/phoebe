package com.example.phoebe.unit.config;

import com.example.phoebe.config.SecurityProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityPropertiesTest {

    @Test
    void securityProperties_ShouldCreateWithAllFields() {
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
    void securityProperties_ShouldHandleNullValues() {
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
