package com.example.phoebe.dto.response;

import com.example.phoebe.model.HomepageMode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublicHomepageResponseDto {
    private HomepageMode mode;
    private List<PublicNewsDto> news;
    private List<PublicHomepageBlockDto> blocks;
}
