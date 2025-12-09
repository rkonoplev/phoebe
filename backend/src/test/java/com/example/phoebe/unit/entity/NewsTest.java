package com.example.phoebe.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the News entity.
 */
class NewsTest {

    @Test
    void defaultConstructorShouldInitializeFields() {
        News news = new News();
        
        assertNull(news.getId());
        assertNull(news.getTitle());
        assertNull(news.getBody());
        assertNull(news.getTeaser());
        assertNull(news.getPublicationDate());
        assertFalse(news.isPublished());
        assertNull(news.getAuthor());
        assertNotNull(news.getTerms());
        assertTrue(news.getTerms().isEmpty());
    }

    @Test
    void settersAndGettersShouldWork() {
        News news = new News();
        User author = new User("testuser", "pass", "email@test.com", true);
        LocalDateTime now = LocalDateTime.now();
        
        news.setId(1L);
        news.setTitle("Test Title");
        news.setBody("Test Body");
        news.setTeaser("Test Teaser");
        news.setPublicationDate(now);
        news.setPublished(true);
        news.setAuthor(author);
        
        assertEquals(1L, news.getId());
        assertEquals("Test Title", news.getTitle());
        assertEquals("Test Body", news.getBody());
        assertEquals("Test Teaser", news.getTeaser());
        assertEquals(now, news.getPublicationDate());
        assertTrue(news.isPublished());
        assertEquals(author, news.getAuthor());
    }

    @Test
    void setTermsShouldWork() {
        News news = new News();
        Term term1 = new Term("Technology", "category");
        Term term2 = new Term("Java", "tag");
        
        Set<Term> terms = new HashSet<>();
        terms.add(term1);
        terms.add(term2);
        
        news.setTerms(terms);
        
        assertEquals(2, news.getTerms().size());
        assertTrue(news.getTerms().contains(term1));
        assertTrue(news.getTerms().contains(term2));
    }

    @Test
    void testEqualsWithNullId() {
        News news1 = new News();
        News news2 = new News();
        
        assertNotEquals(news1, news2);
    }

    @Test
    void testEqualsWithSameId() {
        News news1 = new News();
        News news2 = new News();
        
        news1.setId(1L);
        news2.setId(1L);
        
        assertEquals(news1, news2);
    }

    @Test
    void testEqualsWithDifferentId() {
        News news1 = new News();
        News news2 = new News();
        
        news1.setId(1L);
        news2.setId(2L);
        
        assertNotEquals(news1, news2);
    }

    @Test
    void testEqualsSameObject() {
        News news = new News();
        assertEquals(news, news);
    }

    @Test
    void testEqualsNull() {
        News news = new News();
        assertNotEquals(news, null);
    }

    @Test
    void testEqualsDifferentClass() {
        News news = new News();
        assertNotEquals(news, "Not a News object");
    }

    @Test
    void testEqualsOneNullId() {
        News news1 = new News();
        News news2 = new News();
        
        news1.setId(1L);
        
        assertNotEquals(news1, news2);
        assertNotEquals(news2, news1);
    }

    @Test
    void testHashCodeWithNullId() {
        News news1 = new News();
        News news2 = new News();
        
        assertEquals(news1.hashCode(), news2.hashCode());
    }

    @Test
    void testHashCodeWithSameId() {
        News news1 = new News();
        News news2 = new News();
        
        news1.setId(1L);
        news2.setId(1L);
        
        assertEquals(news1.hashCode(), news2.hashCode());
    }

    @Test
    void testHashCodeConsistency() {
        News news = new News();
        news.setId(1L);
        
        int hash1 = news.hashCode();
        int hash2 = news.hashCode();
        
        assertEquals(hash1, hash2);
    }

    @Test
    void toStringShouldContainKeyFields() {
        News news = new News();
        User author = new User("testuser", "pass", "email@test.com", true);
        author.setId(10L);
        
        news.setId(1L);
        news.setTitle("Test Title");
        news.setPublished(true);
        news.setAuthor(author);
        
        String result = news.toString();
        
        assertTrue(result.contains("News{"));
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("Test Title"));
        assertTrue(result.contains("published=true"));
    }

    @Test
    void toStringShouldHandleNullFields() {
        News news = new News();
        
        assertDoesNotThrow(() -> news.toString());
        String result = news.toString();
        assertTrue(result.contains("News{"));
    }

    @Test
    void publishedDefaultsToFalse() {
        News news = new News();
        assertFalse(news.isPublished());
    }

    @Test
    void termsCollectionIsInitialized() {
        News news = new News();
        assertNotNull(news.getTerms());
        assertEquals(0, news.getTerms().size());
    }

    @Test
    void canAddMultipleTerms() {
        News news = new News();
        Term term1 = new Term("Tech", "category");
        Term term2 = new Term("Java", "tag");
        Term term3 = new Term("Spring", "tag");
        
        news.getTerms().add(term1);
        news.getTerms().add(term2);
        news.getTerms().add(term3);
        
        assertEquals(3, news.getTerms().size());
    }

    @Test
    void canRemoveTerms() {
        News news = new News();
        Term term = new Term("Tech", "category");
        
        news.getTerms().add(term);
        assertEquals(1, news.getTerms().size());
        
        news.getTerms().remove(term);
        assertEquals(0, news.getTerms().size());
    }
}
