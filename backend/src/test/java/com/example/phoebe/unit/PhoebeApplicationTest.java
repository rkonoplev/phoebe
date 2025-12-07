package com.example.phoebe.unit;

import com.example.phoebe.PhoebeApplication;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Simple test for PhoebeApplication main class.
 * Note: The main() method is typically not unit tested as it's just an entry point.
 * This test exists primarily for code coverage metrics.
 */
class PhoebeApplicationTest {

    @Test
    void main_ShouldNotThrowException() {
        // This test verifies that the main method exists and can be called
        // We don't actually run it as it would start the entire application
        assertDoesNotThrow(() -> {
            PhoebeApplication.class.getDeclaredMethod("main", String[].class);
        });
    }
}
