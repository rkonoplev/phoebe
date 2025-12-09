package com.example.phoebe.unit.controller;

import com.example.phoebe.controller.AdminTermController;
import com.example.phoebe.dto.request.TermCreateRequestDto;
import com.example.phoebe.dto.request.TermUpdateRequestDto;
import com.example.phoebe.dto.response.TermResponseDto;
import com.example.phoebe.entity.Term;
import com.example.phoebe.mapper.TermMapper;
import com.example.phoebe.service.TermService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTermControllerTest {

    @Mock
    private TermService termService;

    @Mock
    private TermMapper termMapper;

    @InjectMocks
    private AdminTermController controller;

    private Term term;
    private TermResponseDto termResponseDto;

    @BeforeEach
    void setUp() {
        term = new Term("Test Term", "category");
        term.setId(1L);

        termResponseDto = new TermResponseDto(1L, "Test Term", "category");
    }

    @Test
    void getAllTermsShouldReturnPageOfTerms() {
        Page<Term> termPage = new PageImpl<>(List.of(term));
        when(termService.findAll(any())).thenReturn(termPage);
        when(termMapper.toResponse(any())).thenReturn(termResponseDto);

        ResponseEntity<Page<TermResponseDto>> response = controller.getAllTerms(PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void getTermByIdShouldReturnTerm() {
        when(termService.findById(1L)).thenReturn(term);
        when(termMapper.toResponse(term)).thenReturn(termResponseDto);

        ResponseEntity<TermResponseDto> response = controller.getTermById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Term", response.getBody().name());
    }

    @Test
    void createTermShouldReturnCreatedTerm() {
        TermCreateRequestDto createDto = new TermCreateRequestDto("New Term", "category");
        when(termMapper.toEntity(any())).thenReturn(term);
        when(termService.save(any())).thenReturn(term);
        when(termMapper.toResponse(any())).thenReturn(termResponseDto);

        ResponseEntity<TermResponseDto> response = controller.createTerm(createDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Test Term", response.getBody().name());
    }

    @Test
    void updateTermShouldReturnUpdatedTerm() {
        TermUpdateRequestDto updateDto = new TermUpdateRequestDto("Updated Term", "category");
        when(termService.findById(1L)).thenReturn(term);
        when(termService.save(any())).thenReturn(term);
        when(termMapper.toResponse(any())).thenReturn(termResponseDto);

        ResponseEntity<TermResponseDto> response = controller.updateTerm(1L, updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(termMapper).updateEntity(eq(term), eq(updateDto));
    }

    @Test
    void deleteTermShouldReturnNoContent() {
        ResponseEntity<Void> response = controller.deleteTerm(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(termService).deleteById(1L);
    }
}
