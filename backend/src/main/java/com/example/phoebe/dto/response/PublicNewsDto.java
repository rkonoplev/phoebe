package com.example.phoebe.dto.response;

import java.time.ZonedDateTime;

public class PublicNewsDto {
    private Integer id;
    private String title;
    private String slug;
    private String teaser;
    private ZonedDateTime publishedAt;

    // Constructors
    public PublicNewsDto() {}

    public PublicNewsDto(Integer id, String title, String slug, String teaser, ZonedDateTime publishedAt) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.teaser = teaser;
        this.publishedAt = publishedAt;
    }

    // Getters
    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public String getSlug() { return slug; }
    public String getTeaser() { return teaser; }
    public ZonedDateTime getPublishedAt() { return publishedAt; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setTeaser(String teaser) { this.teaser = teaser; }
    public void setPublishedAt(ZonedDateTime publishedAt) { this.publishedAt = publishedAt; }
}
