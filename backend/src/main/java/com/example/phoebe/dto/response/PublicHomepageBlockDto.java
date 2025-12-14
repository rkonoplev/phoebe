package com.example.phoebe.dto.response;

import com.example.phoebe.model.HomePageBlockType;
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

    public PublicHomepageBlockDto() {
    }

    public PublicHomepageBlockDto(Integer id, Integer weight, HomePageBlockType blockType, String content, List<PublicNewsDto> news, Boolean showTeaser, String titleFontSize) {
        this.id = id;
        this.weight = weight;
        this.blockType = blockType;
        this.content = content;
        this.news = news;
        this.showTeaser = showTeaser;
        this.titleFontSize = titleFontSize;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<PublicNewsDto> getNews() {
        return news;
    }

    public void setNews(List<PublicNewsDto> news) {
        this.news = news;
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
}
