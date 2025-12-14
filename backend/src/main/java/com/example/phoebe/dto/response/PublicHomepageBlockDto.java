package com.example.phoebe.dto.response;

import com.example.phoebe.model.HomePageBlockType;
import com.example.phoebe.dto.response.PublicNewsDto;

import java.util.List;

public class PublicHomepageBlockDto {
    private Integer id;
    private Integer weight;
    private HomePageBlockType blockType;

    // For WIDGET_BLOCK
    private String content;

    // For NEWS_BLOCK
    private List<PublicNewsDto> news;
    private Boolean showTeaser;
    private String titleFontSize;

    // Constructors
    public PublicHomepageBlockDto() {}

    // Getters
    public Integer getId() { return id; }
    public Integer getWeight() { return weight; }
    public HomePageBlockType getBlockType() { return blockType; }
    public String getContent() { return content; }
    public List<PublicNewsDto> getNews() { return news; }
    public Boolean getShowTeaser() { return showTeaser; }
    public String getTitleFontSize() { return titleFontSize; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setWeight(Integer weight) { this.weight = weight; }
    public void setBlockType(HomePageBlockType blockType) { this.blockType = blockType; }
    public void setContent(String content) { this.content = content; }
    public void setNews(List<PublicNewsDto> news) { this.news = news; }
    public void setShowTeaser(Boolean showTeaser) { this.showTeaser = showTeaser; }
    public void setTitleFontSize(String titleFontSize) { this.titleFontSize = titleFontSize; }
}
