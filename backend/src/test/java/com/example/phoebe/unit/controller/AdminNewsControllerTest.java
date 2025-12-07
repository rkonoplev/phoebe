package com.example.phoebe.unit.controller;

import com.example.phoebe.controller.AdminNewsController;

import com.example.phoebe.dto.request.BulkActionRequestDto;
import com.example.phoebe.dto.request.NewsCreateRequestDto;
import com.example.phoebe.dto.request.NewsUpdateRequestDto;
import com.example.phoebe.dto.response.NewsDto;
import com.example.phoebe.service.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminNewsControllerTest {

    @Mock
    private NewsService newsService;

    @Mock
    private Authentication auth;

    @InjectMocks
    private AdminNewsController controller;

    private NewsDto sampleNewsDto;

    @BeforeEach
    void setUp() {
        sampleNewsDto = new NewsDto(
            1L,
            "Test Title",
            "Test Content",
            null,
            LocalDateTime.now(),
            true,
            10L,
            "author",
            Collections.emptySet()
        );
    }

    @Test
    void createShouldReturnCreatedNews() {
        NewsCreateRequestDto createRequest = new NewsCreateRequestDto();
        createRequest.setTitle("Test Title");
        createRequest.setContent("Test Content");

        when(newsService.create(any(NewsCreateRequestDto.class), eq(auth))).thenReturn(sampleNewsDto);

        ResponseEntity<NewsDto> response = controller.create(createRequest, auth);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Test Title", response.getBody().getTitle());
    }

    @Test
    void updateShouldReturnUpdatedNews() {
        NewsUpdateRequestDto updateRequest = new NewsUpdateRequestDto("Updated Title", "Updated Content", null, true, null);
        when(newsService.update(eq(1L), any(NewsUpdateRequestDto.class), eq(auth))).thenReturn(sampleNewsDto);

        ResponseEntity<NewsDto> response = controller.update(1L, updateRequest, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Title", response.getBody().getTitle());
    }

    @Test
    void deleteShouldReturnNoContent() {
        ResponseEntity<Void> response = controller.delete(1L, auth);

        verify(newsService).delete(eq(1L), eq(auth));
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void performBulkActionShouldReturnResult() {
        BulkActionRequestDto request = new BulkActionRequestDto();
        BulkActionRequestDto.BulkActionResult actionResult = new BulkActionRequestDto.BulkActionResult(5);
        when(newsService.performBulkAction(eq(request), eq(auth))).thenReturn(actionResult);

        ResponseEntity<BulkActionRequestDto.BulkActionResult> response = controller.performBulkAction(request, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5, response.getBody().getAffectedCount());
    }
}
