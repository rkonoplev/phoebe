package com.example.phoebe.integration.security;

import com.example.phoebe.entity.News;
import com.example.phoebe.entity.Role;
import com.example.phoebe.entity.User;
import com.example.phoebe.integration.BaseIntegrationTest;
import com.example.phoebe.repository.NewsRepository;
import com.example.phoebe.repository.RoleRepository;
import com.example.phoebe.repository.UserRepository;
import com.example.phoebe.security.AuthorVerification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for role-based security and content ownership verification.
 * These tests verify that ADMINs can access any content, while EDITORs can only access their own.
 */
@Transactional // Rolls back database changes after each test
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class RoleBasedSecurityTest extends BaseIntegrationTest {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthorVerification authorVerification;

    private User adminUser;
    private User editorUser;
    private News editorsNews;

    /**
     * Sets up a standard fixture for security tests.
     * Creates and persists ADMIN and EDITOR roles, an admin user, an editor user,
     * and a news article authored by the editor.
     */
    @BeforeEach
    void setUp() {
        newsRepository.deleteAll();
        
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));
        Role editorRole = roleRepository.findByName("EDITOR")
                .orElseThrow(() -> new IllegalStateException("EDITOR role not found"));

        String uniqueId = String.valueOf(System.currentTimeMillis());
        
        adminUser = new User("role_admin_" + uniqueId, "pass", "role_admin_" + uniqueId + "@test.com", true);
        adminUser.setRoles(java.util.Set.of(adminRole));
        userRepository.save(adminUser);

        editorUser = new User("role_editor_" + uniqueId, "pass", "role_editor_" + uniqueId + "@test.com", true);
        editorUser.setRoles(java.util.Set.of(editorRole));
        userRepository.save(editorUser);

        editorsNews = new News();
        editorsNews.setTitle("Editor's News");
        editorsNews.setBody("Content");
        editorsNews.setAuthor(editorUser);
        newsRepository.save(editorsNews);

        SecurityContextHolder.clearContext();
    }

    @Test
    void testAdminCanAccessAnyContent() {
        // Given: an admin is authenticated
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(adminUser.getUsername(), "password")
        );

        // When & Then: admin should have access to news authored by the editor
        assertTrue(authorVerification.hasAccessToNews(adminUser, editorsNews.getId()));
    }

    @Test
    void testEditorCanOnlyAccessOwnContent() {
        // Given: an editor is authenticated
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(editorUser.getUsername(), "password")
        );

        // When & Then: editor should have access to their own news
        assertTrue(authorVerification.hasAccessToNews(editorUser, editorsNews.getId()));

        // And when another user's news is created
        News otherNews = new News();
        otherNews.setTitle("Admin News");
        otherNews.setBody("Admin Content");
        otherNews.setAuthor(adminUser);
        newsRepository.save(otherNews);

        // Then: editor should NOT have access to it
        assertFalse(authorVerification.hasAccessToNews(editorUser, otherNews.getId()));
    }

    @Test
    void testAuthorVerificationMethod() {
        // When & Then: verify that the isAuthor check is correct for the actual author
        assertTrue(authorVerification.isAuthor(editorUser, editorsNews));

        // And when & Then: verify that the check is correct for a different user
        assertFalse(authorVerification.isAuthor(adminUser, editorsNews));
    }
}
