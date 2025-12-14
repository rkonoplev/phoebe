package com.example.phoebe.dto.response;

import com.example.phoebe.model.HomepageMode;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublicHomepageResponseDto {
    private HomepageMode mode;
    private List<PublicNewsDto> news;
    private List<PublicHomepageBlockDto> blocks;

    // Constructors
    public PublicHomepageResponseDto() {}

    // Getters
    public HomepageMode getMode() { return mode; }
    public List<PublicNewsDto> getNews() { return news; }
    public List<PublicHomepageBlockDto> getBlocks() { return blocks; }

    // Setters
    public void setMode(HomepageMode mode) { this.mode = mode; }
    public void setNews(List<PublicNewsDto> news) { this.news = news; }
    public void setBlocks(List<PublicHomepageBlockDto> blocks) { this.blocks = blocks; }
}
