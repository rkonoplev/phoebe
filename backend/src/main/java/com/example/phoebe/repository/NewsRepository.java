package com.example.phoebe.repository;

import com.example.phoebe.entity.News;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository for managing News entities.
 *
 * Notes:
 * - Queries over ManyToMany (news.terms) use DISTINCT and a dedicated countQuery
 *   to avoid duplicates and incorrect paging.
 * - Bulk update is marked as @Modifying with automatic clear and flush to avoid
 *   stale persistence context.
 * - Read-only transaction on the interface optimizes reads and prevents
 *   accidental flushes. The write method overrides it with its own transaction.
 */
@Repository
@Transactional(readOnly = true)
public interface NewsRepository extends JpaRepository<News, Long> {

    // === Read Operations ===

    @EntityGraph(attributePaths = {"author", "terms"})
    Page<News> findByPublished(boolean published, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "terms"})
    Optional<News> findByIdAndPublished(Long id, boolean published);

    /**
     * Find news by term ID and publication status.
     * Uses DISTINCT to prevent duplicate rows caused by ManyToMany joins.
     */
    @EntityGraph(attributePaths = {"author", "terms"})
    @Query(
            value =
                    "SELECT DISTINCT n FROM News n JOIN n.terms t " +
                            "WHERE n.published = :published AND t.id = :termId",
            countQuery =
                    "SELECT COUNT(DISTINCT n.id) FROM News n JOIN n.terms t " +
                            "WHERE n.published = :published AND t.id = :termId"
    )
    Page<News> findByTermsIdAndPublished(
            @Param("termId") Long termId,
            @Param("published") boolean published,
            Pageable pageable
    );

    /**
     * Find news by multiple term IDs and publication status.
     * Uses DISTINCT and a correct count query for stable paging.
     */
    @EntityGraph(attributePaths = {"author", "terms"})
    @Query(
            value =
                    "SELECT DISTINCT n FROM News n JOIN n.terms t " +
                            "WHERE n.published = :published AND t.id IN :termIds",
            countQuery =
                    "SELECT COUNT(DISTINCT n.id) FROM News n JOIN n.terms t " +
                            "WHERE n.published = :published AND t.id IN :termIds"
    )
    Page<News> findByTermsIdInAndPublished(
            @Param("termIds") List<Long> termIds,
            @Param("published") boolean published,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"author", "terms"})
    @Query("SELECT n FROM News n JOIN n.terms t WHERE t.id IN :termIds")
    List<News> findNewsByTaxonomyTerms(@Param("termIds") Set<Long> termIds, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "terms"})
    Page<News> findByAuthorId(Long authorId, Pageable pageable);

    boolean existsByIdAndAuthorId(Long id, Long authorId);

    // === Bulk Operation Helpers ===

    /**
     * Returns all news IDs. Consider pagination or streaming for large datasets.
     */
    @Query("SELECT n.id FROM News n")
    List<Long> findAllIds();

    /**
     * Returns IDs of news that have the specified term.
     * DISTINCT protects from duplicates in the join table.
     */
    @Query("SELECT DISTINCT n.id FROM News n JOIN n.terms t WHERE t.id = :termId")
    List<Long> findIdsByTermId(@Param("termId") Long termId);

    /**
     * Returns IDs of news created by the given author.
     */
    @Query("SELECT n.id FROM News n WHERE n.author.id = :authorId")
    List<Long> findIdsByAuthorId(@Param("authorId") Long authorId);

    /**
     * Bulk unpublish by IDs.
     * clearAutomatically evicts potentially stale entities from the persistence context.
     * Returns the number of updated rows.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE News n SET n.published = false WHERE n.id IN :ids AND n.published = true")
    int unpublishByIds(@Param("ids") List<Long> ids);
}