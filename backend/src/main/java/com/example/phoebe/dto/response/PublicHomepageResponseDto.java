package com.example.phoebe.dto.response;

import com.example.phoebe.model.HomepageMode;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublicHomepageResponseDto {
    private HomepageMode mode;
    private List<PublicNewsDto> news;
    private List<PublicHomepageBlockDto> blocks;

    public PublicHomepageResponseDto() {
    }

    public HomepageMode getMode() {
        return mode;
    }

    public void setMode(HomepageMode mode) {
        this.mode = mode;
    }

    public List<PublicNewsDto> getNews() {
        return news;
    }

    public void setNews(List<PublicNewsDto> news) {
        this.news = news;
    }

    public List<PublicHomepageBlockDto> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<PublicHomepageBlockDto> blocks) {
        this.blocks = blocks;
    }
}
