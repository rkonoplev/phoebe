package com.example.phoebe.integration.entity;

import com.example.phoebe.entity.News;
import com.example.phoebe.entity.Term;
import com.example.phoebe.entity.User;
import com.example.phoebe.integration.BaseIntegrationTest;
import com.example.phoebe.repository.NewsRepository;
import com.example.phoebe.repository.TermRepository;
import com.example.phoebe.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for the News entity, focusing on persistence, auditing, and lifecycle behaviors.
 * This test uses the full application context with a real database via Testcontainers.
 */
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class NewsTest extends BaseIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TermRepository termRepository;

    private User author;
    private Term term;

    @BeforeEach
    void setUp() {
        // Clean up to ensure test isolation
        newsRepository.deleteAll();
        
        // Create test user with unique username to avoid conflicts
        String uniqueUsername = "testuser_" + System.currentTimeMillis();
        String uniqueEmail = "test_" + System.currentTimeMillis() + "@example.com";
        author = new User(uniqueUsername, "{noop}password", uniqueEmail, true);
        userRepository.save(author);

        // Create test term with unique name to avoid conflicts
        String uniqueTermName = "Technology_" + System.currentTimeMillis();
        term = new Term(uniqueTermName, "category");
        termRepository.save(term);
    }

    /**
     * Verifies getters and setters assign and return expected values.
     */
    @Test
    void gettersAndSetters() {
        News news = new News();

        news.setId(1L);
        assertEquals(1L, news.getId());

        news.setTitle("Test Title");
        assertEquals("Test Title", news.getTitle());

        news.setBody("Test body content");
        assertEquals("Test body content", news.getBody());

        news.setTeaser("Test teaser");
        assertEquals("Test teaser", news.getTeaser());

        LocalDateTime now = LocalDateTime.now();
        news.setPublicationDate(now);
        assertEquals(now, news.getPublicationDate());

        news.setPublished(true);
        assertTrue(news.isPublished());

        news.setAuthor(author);
        assertEquals(author, news.getAuthor());

        Set<Term> terms = new HashSet<>();
        terms.add(term);
        news.setTerms(terms);
        assertEquals(terms, news.getTerms());
    }

    /**
     * Verifies default values for a new instance before it is persisted.
     */
    @Test
    void defaultValuesBeforePersist() {
        News news = new News();
        assertFalse(news.isPublished());
        assertNotNull(news.getTerms());
        assertTrue(news.getTerms().isEmpty());
    }

    /**
     * Verifies that persistence sets publicationDate if it was null and
     * auditing fills createdAt and updatedAt.
     */
    @Test
    void persistSetsPublicationDateAndAuditingTimestamps() {
        News news = buildNews("Title A", author);

        newsRepository.saveAndFlush(news);
        entityManager.clear();

        News reloaded = newsRepository.findById(news.getId()).orElseThrow();

        assertNotNull(reloaded.getPublicationDate());
        assertNotNull(reloaded.getCreatedAt());
        assertNotNull(reloaded.getUpdatedAt());
    }

    /**
     * Ensures that an existing publicationDate is not overwritten on persist.
     */
    @Test
    void persistKeepsExistingPublicationDate() {
        LocalDateTime existing = LocalDateTime.of(2023, 1, 1, 12, 0);

        News news = buildNews("Title B", author);
        news.setPublicationDate(existing);

        newsRepository.saveAndFlush(news);
        entityManager.clear();

        News reloaded = newsRepository.findById(news.getId()).orElseThrow();
        assertEquals(existing, reloaded.getPublicationDate());
    }

    /**
     * Verifies that updatedAt changes on update and version increases.
     * Some databases store timestamps with second granularity; we assert
     * that the timestamp does not go backward and that version increases.
     */
    @Test
    void updatedAtChangesOnUpdateAndVersionIncrements() throws InterruptedException {
        News news = buildNews("Title C", author);
        newsRepository.saveAndFlush(news);

        Long id = news.getId();
        Long v1 = news.getVersion();
        LocalDateTime t1 = news.getUpdatedAt();

        // Small delay to avoid same-second timestamps on some DBs
        Thread.sleep(5);

        News managed = newsRepository.findById(id).orElseThrow();
        managed.setTitle("Title C Updated");
        newsRepository.saveAndFlush(managed);
        entityManager.clear();

        News reloaded = newsRepository.findById(id).orElseThrow();
        Long v2 = reloaded.getVersion();
        LocalDateTime t2 = reloaded.getUpdatedAt();

        assertTrue(v2 > v1);
        assertNotNull(t1);
        assertNotNull(t2);
        assertTrue(!t2.isBefore(t1));
    }

    /**
     * Tests equality logic based on non-null ID.
     * Objects with null ID are not equal.
     */
    @Test
    void equalsById() {
        News n1 = new News();
        News n2 = new News();

        assertNotEquals(n1, n2);

        n1.setId(1L);
        n2.setId(1L);
        assertEquals(n1, n2);

        n2.setId(2L);
        assertNotEquals(n1, n2);

        n2.setId(null);
        assertNotEquals(n1, n2);
    }

    /**
     * Tests hashCode stability and id-based equality.
     */
    @Test
    void hashCodeContract() {
        News n1 = new News();
        News n2 = new News();

        assertEquals(n1.hashCode(), n2.hashCode());

        n1.setId(1L);
        n2.setId(1L);
        assertEquals(n1.hashCode(), n2.hashCode());
    }

    /**
     * Verifies that toString includes key fields and author info.
     * The test is resilient to formatting differences and author representation.
     */
    @Test
    void toStringIncludesKeyFields() {
        News news = buildNews("Test Title", author);
        news.setId(1L);
        news.setPublished(true);
        news.setPublicationDate(LocalDateTime.of(2023, 1, 1, 12, 0));

        String s = news.toString();

        assertTrue(s.startsWith("News{"), () -> "Unexpected toString: " + s);
        assertTrue(Pattern.compile("\\bid=1\\b").matcher(s).find(),
                () -> "Expected 'id=1' in: " + s);
        // Accept either title='Test Title' or any form containing the title text
        assertTrue(s.contains("Test Title"),
                () -> "Expected title text in: " + s);
        assertTrue(Pattern.compile("\\bpublished=true\\b").matcher(s).find(),
                () -> "Expected 'published=true' in: " + s);
        // Accept author=<id>, author=null, or author=User{...}
        assertTrue(Pattern.compile("author=(?:\\d+|null|User\\{.*\\})").matcher(s).find(),
                () -> "Expected author info in: " + s);
        // If author has id, prefer seeing that exact id somewhere
        if (author.getId() != null) {
            boolean hasExactId =
                    s.contains("author=" + author.getId()) ||
                            (s.contains("User{") && s.contains("id=" + author.getId()));
            assertTrue(hasExactId,
                    () -> "Expected author id " + author.getId() + " in: " + s);
        }
    }

    // === Test helpers ===

    private News buildNews(String title, User authorRef) {
        News n = new News();
        n.setTitle(title);
        n.setAuthor(authorRef);
        return n;
    }
}
