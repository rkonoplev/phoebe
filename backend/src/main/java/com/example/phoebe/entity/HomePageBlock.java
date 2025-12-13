package com.example.phoebe.entity;

import com.example.phoebe.model.HomePageBlockType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "home_page_block")
public class HomePageBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer weight = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HomePageBlockType blockType;

    // --- News Block Fields ---
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "home_page_block_taxonomy_term",
            joinColumns = @JoinColumn(name = "home_page_block_id"),
            inverseJoinColumns = @JoinColumn(name = "taxonomy_term_id")
    )
    private Set<TaxonomyTerm> taxonomyTerms;

    private Integer newsCount;

    private Boolean showTeaser;

    private String titleFontSize;

    // --- Widget Block Fields ---
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;
}
