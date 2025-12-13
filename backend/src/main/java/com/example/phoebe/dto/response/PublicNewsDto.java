package com.example.phoebe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicNewsDto {
    private Integer id;
    private String title;
    private String slug;
    private String teaser;
    private ZonedDateTime publishedAt;
}
