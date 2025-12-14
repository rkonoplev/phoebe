package com.example.phoebe.dto.response;

import java.time.ZonedDateTime;

public class PublicNewsDto {
    private Long id;
    private String title;
    private String slug;
    private String teaser;
    private ZonedDateTime publishedAt;

    public PublicNewsDto() {
    }

    public PublicNewsDto(Long id, String title, String slug, String teaser, ZonedDateTime publishedAt) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.teaser = teaser;
        this.publishedAt = publishedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTeaser() {
        return teaser;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }

    public ZonedDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(ZonedDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
}
