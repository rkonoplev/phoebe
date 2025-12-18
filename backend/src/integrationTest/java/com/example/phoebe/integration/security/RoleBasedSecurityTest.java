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
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for role-based access and content ownership rules.
 *
 * Verifies:
 *  - ADMIN users can access any content
 *  - EDITOR users can access only their own content
 */
@Transactional
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

    @BeforeEach
    void setUp() {
        newsRepository.deleteAll();
        userRepository.deleteAll();

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));
        Role editorRole = roleRepository.findByName("EDITOR")
                .orElseThrow(() -> new IllegalStateException("EDITOR role not found"));

        String suffix = String.valueOf(System.nanoTime());

        adminUser = new User(
                "admin_" + suffix,
                "{noop}password",
                "admin_" + suffix + "@test.com",
                true
        );
        adminUser.setRoles(java.util.Set.of(adminRole));
        userRepository.save(adminUser);

        editorUser = new User(
                "editor_" + suffix,
                "{noop}password",
                "editor_" + suffix + "@test.com",
                true
        );
        editorUser.setRoles(java.util.Set.of(editorRole));
        userRepository.save(editorUser);

        editorsNews = new News();
        editorsNews.setTitle("Editor's News");
        editorsNews.setBody("Content");
        editorsNews.setAuthor(editorUser);
        newsRepository.save(editorsNews);
    }

    @Test
    void adminShouldAccessAnyNews() {
        assertTrue(authorVerification.hasAccessToNews(adminUser, editorsNews.getId()));
    }

    @Test
    void editorShouldAccessOnlyOwnNews() {
        assertTrue(authorVerification.hasAccessToNews(editorUser, editorsNews.getId()));

        News adminNews = new News();
        adminNews.setTitle("Admin News");
        adminNews.setBody("Admin Content");
        adminNews.setAuthor(adminUser);
        newsRepository.save(adminNews);

        assertFalse(authorVerification.hasAccessToNews(editorUser, adminNews.getId()));
    }

    @Test
    void isAuthorShouldWorkCorrectly() {
        assertTrue(authorVerification.isAuthor(editorUser, editorsNews));
        assertFalse(authorVerification.isAuthor(adminUser, editorsNews));
    }
}
