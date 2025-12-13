package com.example.phoebe.dto;

import com.example.phoebe.model.HomePageBlockType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
