package com.example.phoebe.dto;

import com.example.phoebe.model.HomePageBlockType;

import java.util.Set;

public class HomePageBlockDto {
    private Integer id;
    private Integer weight;
    private HomePageBlockType blockType;

    // News Block Fields
    private Set<Integer> taxonomyTermIds;
    private Integer newsCount;
    private Boolean showTeaser;
    private String titleFontSize;

    // Widget Block Fields
    private String content;

    // Constructors
    public HomePageBlockDto() {}

    // Getters
    public Integer getId() { return id; }
    public Integer getWeight() { return weight; }
    public HomePageBlockType getBlockType() { return blockType; }
    public Set<Integer> getTaxonomyTermIds() { return taxonomyTermIds; }
    public Integer getNewsCount() { return newsCount; }
    public Boolean getShowTeaser() { return showTeaser; }
    public String getTitleFontSize() { return titleFontSize; }
    public String getContent() { return content; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setWeight(Integer weight) { this.weight = weight; }
    public void setBlockType(HomePageBlockType blockType) { this.blockType = blockType; }
    public void setTaxonomyTermIds(Set<Integer> taxonomyTermIds) { this.taxonomyTermIds = taxonomyTermIds; }
    public void setNewsCount(Integer newsCount) { this.newsCount = newsCount; }
    public void setShowTeaser(Boolean showTeaser) { this.showTeaser = showTeaser; }
    public void setTitleFontSize(String titleFontSize) { this.titleFontSize = titleFontSize; }
    public void setContent(String content) { this.content = content; }
}
