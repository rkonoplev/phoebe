package com.example.phoebe.service.impl;

import com.example.phoebe.dto.request.NewsCreateRequestDto;
import com.example.phoebe.dto.response.NewsDto;
import com.example.phoebe.entity.News;
import com.example.phoebe.entity.Term;
import com.example.phoebe.entity.User;
import com.example.phoebe.integration.BaseIntegrationTest;
import com.example.phoebe.repository.NewsRepository;
import com.example.phoebe.repository.TermRepository;
import com.example.phoebe.repository.UserRepository;
import com.example.phoebe.service.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for {@link NewsServiceImpl}, focusing on database interactions
 * and security context integration for create, delete, and read operations.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class NewsServiceImplIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private NewsService newsService;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TermRepository termRepository;

    private User testUser;
    private Term testTerm;
    private Authentication auth;

    /**
     * Sets up the test environment before each test.
     * This includes clearing repositories, creating a persistent test user and term,
     * and setting up a security context with an authenticated user.
     */
    @BeforeEach
    void setUp() {
        // Clean up database to ensure test isolation
        newsRepository.deleteAll();
        userRepository.deleteAll();
        termRepository.deleteAll();

        // Create and save a test user to act as the author
        testUser = new User("integration_user", "password", "integration@test.com", true);
        userRepository.save(testUser);

        // Create and save a test term to be associated with news
        testTerm = new Term("Integration Term", "category");
        termRepository.save(testTerm);

        // Mock the security context to simulate an authenticated user
        auth = new UsernamePasswordAuthenticationToken(testUser.getUsername(), "password", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void createNewsShouldSaveAndReturnDto() {
        // Given
        NewsCreateRequestDto request = new NewsCreateRequestDto();
        request.setTitle("Integration Test Title");
        request.setContent("Some content");
        request.setTermIds(Set.of(testTerm.getId()));

        // When
        NewsDto saved = newsService.create(request, auth);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Integration Test Title", saved.getTitle());
        assertEquals(testUser.getId(), saved.getAuthorId());
        assertNotNull(saved.getTermNames());
        assertFalse(saved.getTermNames().isEmpty());
        assertTrue(saved.getTermNames().contains(testTerm.getName()));
    }

    @Test
    void testDeleteNews() {
        // Given
        NewsCreateRequestDto request = new NewsCreateRequestDto();
        request.setTitle("To Be Deleted");
        request.setContent("Content");
        request.setTermIds(Set.of(testTerm.getId()));
        NewsDto saved = newsService.create(request, auth);
        Long newsId = saved.getId();

        // When
        newsService.delete(newsId, SecurityContextHolder.getContext().getAuthentication());

        // Then
        Optional<News> found = newsRepository.findById(newsId);
        assertFalse(found.isPresent());
    }
}
