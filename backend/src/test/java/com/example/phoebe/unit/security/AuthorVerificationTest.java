package com.example.phoebe.unit.security;

import com.example.phoebe.entity.News;
import com.example.phoebe.entity.Role;
import com.example.phoebe.entity.User;
import com.example.phoebe.repository.NewsRepository;
import com.example.phoebe.security.AuthorVerification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorVerificationTest {

    @Mock
    private NewsRepository newsRepository;

    @InjectMocks
    private AuthorVerification authorVerification;

    private User adminUser;
    private User editorUser;
    private User otherUser;
    private News news;

    @BeforeEach
    void setUp() {
        Role adminRole = new Role("ADMIN", "Administrator role");
        adminRole.setId(1L);

        Role editorRole = new Role("EDITOR", "Editor role");
        editorRole.setId(2L);

        adminUser = new User("admin", "password", "admin@test.com", true);
        adminUser.setId(1L);
        adminUser.setRoles(Set.of(adminRole));

        editorUser = new User("editor", "password", "editor@test.com", true);
        editorUser.setId(2L);
        editorUser.setRoles(Set.of(editorRole));

        otherUser = new User("other", "password", "other@test.com", true);
        otherUser.setId(3L);
        otherUser.setRoles(Set.of(editorRole));

        news = new News();
        news.setId(100L);
        news.setAuthor(editorUser);
    }

    @Test
    void hasAccessToNewsAdminUserReturnsTrue() {
        // Admin has access without checking repository
        assertTrue(authorVerification.hasAccessToNews(adminUser, 100L));
        verify(newsRepository, never()).findById(any());
    }

    @Test
    void hasAccessToNewsAuthorUserReturnsTrue() {
        when(newsRepository.findById(100L)).thenReturn(Optional.of(news));

        assertTrue(authorVerification.hasAccessToNews(editorUser, 100L));
    }

    @Test
    void hasAccessToNewsNonAuthorUserReturnsFalse() {
        when(newsRepository.findById(100L)).thenReturn(Optional.of(news));

        assertFalse(authorVerification.hasAccessToNews(otherUser, 100L));
    }

    @Test
    void hasAccessToNewsNullUserReturnsFalse() {
        assertFalse(authorVerification.hasAccessToNews(null, 100L));
        verify(newsRepository, never()).findById(any());
    }

    @Test
    void hasAccessToNewsNullNewsIdReturnsFalse() {
        assertFalse(authorVerification.hasAccessToNews(editorUser, null));
        verify(newsRepository, never()).findById(any());
    }

    @Test
    void hasAccessToNewsNewsNotFoundReturnsFalse() {
        when(newsRepository.findById(999L)).thenReturn(Optional.empty());

        assertFalse(authorVerification.hasAccessToNews(editorUser, 999L));
    }

    @Test
    void isAuthorSameUserReturnsTrue() {
        assertTrue(authorVerification.isAuthor(editorUser, news));
    }

    @Test
    void isAuthorDifferentUserReturnsFalse() {
        assertFalse(authorVerification.isAuthor(otherUser, news));
    }

    @Test
    void isAuthorNullUserReturnsFalse() {
        assertFalse(authorVerification.isAuthor(null, news));
    }

    @Test
    void isAuthorNullNewsReturnsFalse() {
        assertFalse(authorVerification.isAuthor(editorUser, null));
    }

    @Test
    void isAuthorNewsWithoutAuthorReturnsFalse() {
        News newsWithoutAuthor = new News();
        newsWithoutAuthor.setId(200L);

        assertFalse(authorVerification.isAuthor(editorUser, newsWithoutAuthor));
    }

    @Test
    void isAuthorUserWithoutIdReturnsFalse() {
        User userWithoutId = new User("test", "password", "test@test.com", true);
        // ID is null by default

        assertFalse(authorVerification.isAuthor(userWithoutId, news));
    }
}
