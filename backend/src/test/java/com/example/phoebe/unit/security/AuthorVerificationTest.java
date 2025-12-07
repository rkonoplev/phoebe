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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void hasAccessToNews_AdminUser_ReturnsTrue() {
        // Admin has access without checking repository
        assertTrue(authorVerification.hasAccessToNews(adminUser, 100L));
        verify(newsRepository, never()).findById(any());
    }

    @Test
    void hasAccessToNews_AuthorUser_ReturnsTrue() {
        when(newsRepository.findById(100L)).thenReturn(Optional.of(news));

        assertTrue(authorVerification.hasAccessToNews(editorUser, 100L));
    }

    @Test
    void hasAccessToNews_NonAuthorUser_ReturnsFalse() {
        when(newsRepository.findById(100L)).thenReturn(Optional.of(news));

        assertFalse(authorVerification.hasAccessToNews(otherUser, 100L));
    }

    @Test
    void hasAccessToNews_NullUser_ReturnsFalse() {
        assertFalse(authorVerification.hasAccessToNews(null, 100L));
        verify(newsRepository, never()).findById(any());
    }

    @Test
    void hasAccessToNews_NullNewsId_ReturnsFalse() {
        assertFalse(authorVerification.hasAccessToNews(editorUser, null));
        verify(newsRepository, never()).findById(any());
    }

    @Test
    void hasAccessToNews_NewsNotFound_ReturnsFalse() {
        when(newsRepository.findById(999L)).thenReturn(Optional.empty());

        assertFalse(authorVerification.hasAccessToNews(editorUser, 999L));
    }

    @Test
    void isAuthor_SameUser_ReturnsTrue() {
        assertTrue(authorVerification.isAuthor(editorUser, news));
    }

    @Test
    void isAuthor_DifferentUser_ReturnsFalse() {
        assertFalse(authorVerification.isAuthor(otherUser, news));
    }

    @Test
    void isAuthor_NullUser_ReturnsFalse() {
        assertFalse(authorVerification.isAuthor(null, news));
    }

    @Test
    void isAuthor_NullNews_ReturnsFalse() {
        assertFalse(authorVerification.isAuthor(editorUser, null));
    }

    @Test
    void isAuthor_NewsWithoutAuthor_ReturnsFalse() {
        News newsWithoutAuthor = new News();
        newsWithoutAuthor.setId(200L);

        assertFalse(authorVerification.isAuthor(editorUser, newsWithoutAuthor));
    }

    @Test
    void isAuthor_UserWithoutId_ReturnsFalse() {
        User userWithoutId = new User("test", "password", "test@test.com", true);
        // ID is null by default

        assertFalse(authorVerification.isAuthor(userWithoutId, news));
    }
}
