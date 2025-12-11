package com.example.phoebe.integration.dto.request;

import com.example.phoebe.dto.request.NewsUpdateRequestDto;
import com.example.phoebe.entity.User;
import com.example.phoebe.integration.BaseIntegrationTest;
import com.example.phoebe.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class NewsUpdateRequestDtoIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private Validator validator;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        String uniqueId = String.valueOf(System.currentTimeMillis());
        testUser = new User("test_updater_" + uniqueId, "pass", "updater_" + uniqueId + "@test.com", true);
        userRepository.save(testUser);
    }

    @Test
    void updateWithValidDtoShouldHaveNoViolations() {
        NewsUpdateRequestDto dto = new NewsUpdateRequestDto("Valid Title", "Valid content.", null, true, null);

        Set<ConstraintViolation<NewsUpdateRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void titleTooLongShouldHaveViolation() {
        NewsUpdateRequestDto dto = new NewsUpdateRequestDto("a".repeat(51), null, null, true, null); // Exceeds max 50

        Set<ConstraintViolation<NewsUpdateRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }
}
