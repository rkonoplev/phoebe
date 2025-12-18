package com.example.phoebe.integration.dto.request;

import com.example.phoebe.dto.request.ChannelSettingsUpdateDto;
import com.example.phoebe.integration.BaseIntegrationTest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChannelSettingsValidationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private Validator validator;

    @Test
    void validChannelSettingsShouldHaveNoViolations() {
        ChannelSettingsUpdateDto dto = new ChannelSettingsUpdateDto(
                "Test Site",
                "Test description",
                "test, keywords",
                "<p>Header content</p>",
                "https://example.com/logo.png",
                "<p>Footer content</p>",
                "[1,2,3]",
                "https://example.com"
        );

        Set<ConstraintViolation<ChannelSettingsUpdateDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidUrlShouldHaveViolation() {
        ChannelSettingsUpdateDto dto = new ChannelSettingsUpdateDto(
                null, null, null, null, null, null, null,
                "invalid-url"
        );

        Set<ConstraintViolation<ChannelSettingsUpdateDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("valid HTTP/HTTPS URL")));
    }

    @Test
    void invalidJsonArrayShouldHaveViolation() {
        ChannelSettingsUpdateDto dto = new ChannelSettingsUpdateDto(
                null, null, null, null, null, null,
                "invalid-json",
                null
        );

        Set<ConstraintViolation<ChannelSettingsUpdateDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("valid JSON array")));
    }
}