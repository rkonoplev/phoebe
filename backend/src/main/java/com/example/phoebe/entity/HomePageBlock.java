package com.example.phoebe.entity;

import com.example.phoebe.model.HomePageBlockType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import com.example.phoebe.entity.Term;

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

    public HomePageBlock() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public HomePageBlockType getBlockType() {
        return blockType;
    }

    public void setBlockType(HomePageBlockType blockType) {
        this.blockType = blockType;
    }

    public Set<Term> getTaxonomyTerms() {
        return taxonomyTerms;
    }

    public void setTaxonomyTerms(Set<Term> taxonomyTerms) {
        this.taxonomyTerms = taxonomyTerms;
    }

    public Integer getNewsCount() {
        return newsCount;
    }

    public void setNewsCount(Integer newsCount) {
        this.newsCount = newsCount;
    }

    public Boolean getShowTeaser() {
        return showTeaser;
    }

    public void setShowTeaser(Boolean showTeaser) {
        this.showTeaser = showTeaser;
    }

    public String getTitleFontSize() {
        return titleFontSize;
    }

    public void setTitleFontSize(String titleFontSize) {
        this.titleFontSize = titleFontSize;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
