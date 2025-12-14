package com.example.phoebe.dto;

import com.example.phoebe.model.HomePageBlockType;
import java.util.Set;

public class HomePageBlockDto {
    private Integer id;
    private Integer weight;
    private HomePageBlockType blockType;
    private Set<Long> taxonomyTermIds; // Changed from Integer to Long
    private Integer newsCount;
    private Boolean showTeaser;
    private String titleFontSize;
    private String content;

    public HomePageBlockDto() {
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

    public Set<Long> getTaxonomyTermIds() { // Changed from Integer to Long
        return taxonomyTermIds;
    }

    public void setTaxonomyTermIds(Set<Long> taxonomyTermIds) { // Changed from Integer to Long
        this.taxonomyTermIds = taxonomyTermIds;
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
