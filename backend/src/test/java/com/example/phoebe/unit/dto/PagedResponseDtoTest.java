package com.example.phoebe.unit.dto;

import com.example.phoebe.dto.response.PagedResponseDto;
import com.example.phoebe.value.PaginationInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PagedResponseDtoTest {

    @Test
    void pagedResponseDto_ShouldCreateWithContentAndPagination() {
        PaginationInfo pagination = new PaginationInfo(0, 10, 100L, 10, true, false);
        List<String> content = List.of("item1", "item2", "item3");

        PagedResponseDto<String> dto = new PagedResponseDto<>(content, pagination);

        assertEquals(3, dto.content().size());
        assertEquals("item1", dto.content().get(0));
        assertEquals(0, dto.pagination().currentPage());
        assertEquals(10, dto.pagination().pageSize());
        assertEquals(100L, dto.pagination().totalElements());
        assertEquals(10, dto.pagination().totalPages());
    }

    @Test
    void pagedResponseDto_ShouldHandleEmptyContent() {
        PaginationInfo pagination = new PaginationInfo(0, 0, 0L, 10, false, false);
        List<String> content = List.of();

        PagedResponseDto<String> dto = new PagedResponseDto<>(content, pagination);

        assertTrue(dto.content().isEmpty());
        assertEquals(0L, dto.pagination().totalElements());
    }
}
