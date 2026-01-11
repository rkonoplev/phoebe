package com.example.phoebe.integration.service.impl;

import com.example.phoebe.dto.request.BulkActionRequestDto;
import com.example.phoebe.dto.request.NewsCreateRequestDto;
import com.example.phoebe.dto.request.NewsUpdateRequestDto;
import com.example.phoebe.dto.response.NewsDto;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for {@link com.example.phoebe.service.impl.NewsServiceImpl}, focusing on database interactions
 * and security context integration for create, delete, update, and bulk operations.
 *
 * Note: The fully qualified class name is used in {@link} to avoid "Cannot resolve symbol" warnings in some IDEs.
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class NewsServiceImplIntegrationTest extends BaseIntegrationTest {

    private static final String TEST_PASSWORD = "password_for_tests";

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
        termRepository.deleteAll();

        // Create and save a test user with unique timestamp to avoid constraint violations
        String timestamp = String.valueOf(System.currentTimeMillis());
        testUser = new User("integration_user_" + timestamp, TEST_PASSWORD, "integration_" + timestamp + "@test.com", true);
        userRepository.save(testUser);

        // Create and save a test term to be associated with news
        testTerm = new Term("Integration Term", "category");
        termRepository.save(testTerm);

        // Mock the security context to simulate an authenticated user
        auth = new UsernamePasswordAuthenticationToken(testUser.getUsername(), TEST_PASSWORD, Collections.emptyList());
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
    void updateNewsShouldModifyExistingEntity() {
        // Given
        NewsCreateRequestDto createRequest = new NewsCreateRequestDto();
        createRequest.setTitle("Original Title");
        createRequest.setContent("Original Content");
        createRequest.setTermIds(Set.of(testTerm.getId()));
        NewsDto created = newsService.create(createRequest, auth);
        Long newsId = created.getId();

        // When
        NewsUpdateRequestDto updateRequest = new NewsUpdateRequestDto(
                "Updated Title",
                "Updated Content",
                "Updated Teaser",
                true,
                Set.of(testTerm.getId())
        );
        NewsDto updated = newsService.update(newsId, updateRequest, auth);

        // Then
        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated Content", updated.getBody());
        assertEquals("Updated Teaser", updated.getTeaser());
        assertTrue(updated.isPublished());
        assertTrue(updated.getTermNames().contains(testTerm.getName()));
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
        assertFalse(newsRepository.findById(newsId).isPresent());
    }

    @Test
    void performBulkActionShouldDeleteMultipleNews() {
        // Given
        // Create two news articles
        NewsCreateRequestDto request1 = new NewsCreateRequestDto();
        request1.setTitle("Bulk News 1");
        request1.setContent("Content 1");
        request1.setTermIds(Set.of(testTerm.getId()));
        NewsDto news1 = newsService.create(request1, auth);

        NewsCreateRequestDto request2 = new NewsCreateRequestDto();
        request2.setTitle("Bulk News 2");
        request2.setContent("Content 2");
        request2.setTermIds(Set.of(testTerm.getId()));
        NewsDto news2 = newsService.create(request2, auth);

        // Prepare bulk action
        BulkActionRequestDto bulkRequest = new BulkActionRequestDto();
        bulkRequest.setAction(BulkActionRequestDto.ActionType.DELETE);
        bulkRequest.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        bulkRequest.setItemIds(Set.of(news1.getId(), news2.getId()));
        bulkRequest.setConfirmed(true);

        // When
        BulkActionRequestDto.BulkActionResult result = newsService.performBulkAction(bulkRequest, auth);

        // Then
        assertEquals(2, result.getAffectedCount());
        assertFalse(newsRepository.findById(news1.getId()).isPresent());
        assertFalse(newsRepository.findById(news2.getId()).isPresent());
    }
}