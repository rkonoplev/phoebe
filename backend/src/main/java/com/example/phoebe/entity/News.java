package com.example.phoebe.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Entity representing a news article. Designed for portability and auditing.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "content",
        indexes = {
                @Index(name = "idx_news_title", columnList = "title"),
                @Index(name = "idx_news_published", columnList = "published"),
                @Index(name = "idx_news_publication_date", columnList = "publication_date"),
                @Index(name = "idx_news_author", columnList = "author_id"),
                @Index(name = "idx_news_published_pubdate", columnList = "published, publication_date")
        }
)
public class News {

    /** Unique identifier for the news article. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Article title. */
    @NotBlank(message = "Title is required")
    @Size(max = 50, message = "Title must not exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String title;

    /** Full body text of the article. Lazy loading for performance. */
    @Column(columnDefinition = "TEXT")
    @Basic(fetch = FetchType.LAZY)
    private String body;

    /** Short teaser text for listing views. */
    @Size(max = 250, message = "Teaser must not exceed 250 characters")
    @Column(columnDefinition = "TEXT")
    @Basic(fetch = FetchType.LAZY)
    private String teaser;

    /** Scheduled or actual publication date. */
    @Column(name = "publication_date", nullable = false)
    private LocalDateTime publicationDate;

    /** Whether the article is publicly visible. */
    @Column(nullable = false)
    private boolean published = false;

    /** Creation timestamp (managed by auditing). */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Update timestamp (managed by auditing). */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** Version for optimistic locking. */
    @Version
    private Long version;

    /** Author of the news article. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /** Terms associated with the article (tags, categories). */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "content_terms",
            joinColumns = @JoinColumn(name = "content_id"),
            inverseJoinColumns = @JoinColumn(name = "term_id")
    )
    private Set<Term> terms = new HashSet<>();

    /** Sets publicationDate if not provided manually before persist. */
    @PrePersist
    private void onCreate() {
        if (publicationDate == null) {
            publicationDate = LocalDateTime.now();
        }
    }

    // Getters

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getTeaser() {
        return teaser;
    }

    public LocalDateTime getPublicationDate() {
        return publicationDate;
    }

    public boolean isPublished() {
        return published;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public User getAuthor() {
        return author;
    }

    public Set<Term> getTerms() {
        return terms;
    }

    // Setters

    public void setId(Long id) {
        this.id = id;
    }



    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }

    public void setPublicationDate(LocalDateTime publicationDate) {
        this.publicationDate = publicationDate;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setTerms(Set<Term> terms) {
        this.terms = terms;
    }

    // === equals & hashCode ===

    /**
     * Implements equality based on the entity's ID.
     * This is suitable for entities without a natural business key.
     * It correctly handles Hibernate proxies.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        News news = (News) o;
        // If the id is null, the objects are not equal.
        // This is a safe choice for entities managed by the persistence context.
        return id != null && id.equals(news.id);
    }

    /**
     * Returns a fixed hash code.
     * This is a trade-off for entities without a stable business key. It avoids
     * hash code changes after persistence but may lead to slightly slower
     * performance in hash-based collections. The performance impact is
     * negligible for most use cases.
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    // toString

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", published=" + published +
                ", publicationDate=" + publicationDate +
                ", author=" + (author != null ? author.getId() : null) +
                '}';
    }
}