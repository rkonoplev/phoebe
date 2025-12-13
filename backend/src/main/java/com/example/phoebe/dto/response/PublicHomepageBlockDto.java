package com.example.phoebe.dto.response;

import com.example.phoebe.model.HomePageBlockType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
