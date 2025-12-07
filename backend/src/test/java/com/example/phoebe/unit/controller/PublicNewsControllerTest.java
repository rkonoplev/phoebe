package com.example.phoebe.unit.controller;

import com.example.phoebe.controller.PublicNewsController;

import com.example.phoebe.dto.response.NewsDto;
import com.example.phoebe.service.NewsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicNewsControllerTest {

    @Mock
    private NewsService newsService;

    @InjectMocks
    private PublicNewsController controller;

    @Test
    void findAllPublishedShouldReturnPageOfNews() {
        NewsDto newsDto = new NewsDto(
            1L,
            "Public Title",
            "Public Content",
            null,
            LocalDateTime.now(),
            true,
            1L,
            "author",
            Collections.emptySet()
        );
        Page<NewsDto> page = new PageImpl<>(List.of(newsDto));
        when(newsService.findAllPublished(any())).thenReturn(page);

        Page<NewsDto> result = controller.findAllPublished(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Public Title", result.getContent().get(0).getTitle());
    }

    @Test
    void findPublishedByIdShouldReturnSingleNews() {
        NewsDto newsDto = new NewsDto(
            1L,
            "Single News",
            "Content",
            null,
            LocalDateTime.now(),
            true,
            1L,
            "author",
            Collections.emptySet()
        );
        when(newsService.findPublishedById(1L)).thenReturn(newsDto);

        NewsDto result = controller.findPublishedById(1L);

        assertEquals("Single News", result.getTitle());
    }
}
