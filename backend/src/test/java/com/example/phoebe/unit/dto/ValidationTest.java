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
        // Test invalid email
        UserCreateRequestDto dto1 = new UserCreateRequestDto();
        dto1.setUsername("validuser");
        dto1.setPassword("somevalidpassword");
        dto1.setEmail("invalid-email");
        Set<ConstraintViolation<UserCreateRequestDto>> violations1 = validator.validate(dto1);
        assertFalse(violations1.isEmpty());
        assertTrue(violations1.stream().anyMatch(v -> v.getMessage().contains("Email must be valid")));

        // Test empty email
        UserCreateRequestDto dto2 = new UserCreateRequestDto();
        dto2.setUsername("validuser");
        dto2.setPassword("somevalidpassword");
        dto2.setEmail("");
        Set<ConstraintViolation<UserCreateRequestDto>> violations2 = validator.validate(dto2);
        assertFalse(violations2.isEmpty());
        assertTrue(violations2.stream().anyMatch(v -> v.getMessage().contains("Email is required")));

        // Test valid email
        UserCreateRequestDto dto3 = new UserCreateRequestDto();
        dto3.setUsername("validuser");
        dto3.setPassword("somevalidpassword");
        dto3.setEmail("user@example.com");
        Set<ConstraintViolation<UserCreateRequestDto>> violations3 = validator.validate(dto3);
        assertTrue(violations3.isEmpty());
    }

    @Test
    void userCreateRequestDtoShouldValidateUsername() {
        // Test empty username
        UserCreateRequestDto dto1 = new UserCreateRequestDto();
        dto1.setEmail("user@example.com");
        dto1.setPassword("somevalidpassword");
        dto1.setUsername("");
        Set<ConstraintViolation<UserCreateRequestDto>> violations1 = validator.validate(dto1);
        assertFalse(violations1.isEmpty());
        assertTrue(violations1.stream().anyMatch(v -> v.getMessage().contains("Username is required")));

        // Test username too short
        UserCreateRequestDto dto2 = new UserCreateRequestDto();
        dto2.setEmail("user@example.com");
        dto2.setPassword("somevalidpassword");
        dto2.setUsername("ab");
        Set<ConstraintViolation<UserCreateRequestDto>> violations2 = validator.validate(dto2);
        assertFalse(violations2.isEmpty());
        assertTrue(violations2.stream().anyMatch(v -> v.getMessage().contains("between 3 and 100")));

        // Test valid username
        UserCreateRequestDto dto3 = new UserCreateRequestDto();
        dto3.setEmail("user@example.com");
        dto3.setPassword("somevalidpassword");
        dto3.setUsername("validuser");
        Set<ConstraintViolation<UserCreateRequestDto>> violations3 = validator.validate(dto3);
        assertTrue(violations3.isEmpty());
    }
}