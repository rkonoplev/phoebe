package com.example.phoebe.entity;

import com.example.phoebe.model.HomePageBlockType;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
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
    private Set<Term> taxonomyTerms = new HashSet<>();

    private Integer newsCount;

    private Boolean showTeaser;

    private String titleFontSize;

    // --- Widget Block Fields ---
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    // Constructors
    public HomePageBlock() {}

    // Getters
    public Integer getId() { return id; }
    public Integer getWeight() { return weight; }
    public HomePageBlockType getBlockType() { return blockType; }
    public Set<Term> getTaxonomyTerms() { return taxonomyTerms; }
    public Integer getNewsCount() { return newsCount; }
    public Boolean getShowTeaser() { return showTeaser; }
    public String getTitleFontSize() { return titleFontSize; }
    public String getContent() { return content; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setWeight(Integer weight) { this.weight = weight; }
    public void setBlockType(HomePageBlockType blockType) { this.blockType = blockType; }
    public void setTaxonomyTerms(Set<Term> taxonomyTerms) { this.taxonomyTerms = taxonomyTerms; }
    public void setNewsCount(Integer newsCount) { this.newsCount = newsCount; }
    public void setShowTeaser(Boolean showTeaser) { this.showTeaser = showTeaser; }
    public void setTitleFontSize(String titleFontSize) { this.titleFontSize = titleFontSize; }
    public void setContent(String content) { this.content = content; }
}
