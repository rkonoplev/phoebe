package com.example.phoebe.dto;

import com.example.phoebe.dto.request.NewsCreateRequestDto;
import com.example.phoebe.dto.request.UserCreateRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for DTO validation constraints.
 */
class ValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void newsCreateRequestDtoShouldValidateTitle() {
        NewsCreateRequestDto dto = new NewsCreateRequestDto();
        dto.setContent("Valid content");

        // Test empty title
        dto.setTitle("");
        Set<ConstraintViolation<NewsCreateRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Title is required")));

        // Test title too long
        dto.setTitle("A".repeat(51));
        violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("50 characters")));

        // Test valid title
        dto.setTitle("Valid Title");
        violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void newsCreateRequestDtoShouldValidateTeaser() {
        NewsCreateRequestDto dto = new NewsCreateRequestDto();
        dto.setTitle("Valid Title");
        dto.setContent("Valid content");

        // Test teaser too long
        dto.setTeaser("A".repeat(251));
        Set<ConstraintViolation<NewsCreateRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("250 characters")));

        // Test unsafe HTML in teaser
        dto.setTeaser("<script>alert('xss')</script>");
        violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("unsafe HTML")));

        // Test safe HTML in teaser
        dto.setTeaser("<b>Bold</b> and <i>italic</i> text");
        violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void userCreateRequestDtoShouldValidateEmail() {
        UserCreateRequestDto dto = new UserCreateRequestDto();
        dto.setUsername("validuser");
        dto.setPassword("somevalidpassword");

        // Test invalid email
        dto.setEmail("invalid-email");
        Set<ConstraintViolation<UserCreateRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email must be valid")));

        // Test empty email
        dto.setEmail("");
        violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));

        // Test valid email
        dto.setEmail("user@example.com");
        violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void userCreateRequestDtoShouldValidateUsername() {
        UserCreateRequestDto dto = new UserCreateRequestDto();
        dto.setEmail("user@example.com");
        dto.setPassword("somevalidpassword");

        // Test empty username
        dto.setUsername("");
        Set<ConstraintViolation<UserCreateRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Username is required")));

        // Test username too short
        dto.setUsername("ab");
        violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("between 3 and 100")));

        // Test valid username
        dto.setUsername("validuser");
        violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}