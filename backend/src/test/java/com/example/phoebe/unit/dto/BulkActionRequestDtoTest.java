package com.example.phoebe.unit.dto;

import com.example.phoebe.dto.request.BulkActionRequestDto;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BulkActionRequestDtoTest {

    @Test
    void bulkActionRequestDto_ShouldSetAndGetAllFields() {
        BulkActionRequestDto dto = new BulkActionRequestDto();
        dto.setAction(BulkActionRequestDto.ActionType.DELETE);
        dto.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        dto.setItemIds(Set.of(1L, 2L, 3L));
        dto.setTermId(10L);
        dto.setAuthorId(20L);
        dto.setConfirmed(true);

        assertEquals(BulkActionRequestDto.ActionType.DELETE, dto.getAction());
        assertEquals(BulkActionRequestDto.FilterType.BY_IDS, dto.getFilterType());
        assertEquals(Set.of(1L, 2L, 3L), dto.getItemIds());
        assertEquals(10L, dto.getTermId());
        assertEquals(20L, dto.getAuthorId());
        assertTrue(dto.isConfirmed());
    }

    @Test
    void bulkActionResult_ShouldReturnAffectedCount() {
        BulkActionRequestDto.BulkActionResult result = new BulkActionRequestDto.BulkActionResult(5);

        assertEquals(5, result.getAffectedCount());
    }

    @Test
    void actionType_ShouldHaveDeleteAndUnpublish() {
        assertNotNull(BulkActionRequestDto.ActionType.DELETE);
        assertNotNull(BulkActionRequestDto.ActionType.UNPUBLISH);
        assertEquals(2, BulkActionRequestDto.ActionType.values().length);
    }

    @Test
    void filterType_ShouldHaveAllTypes() {
        assertNotNull(BulkActionRequestDto.FilterType.BY_IDS);
        assertNotNull(BulkActionRequestDto.FilterType.BY_TERM);
        assertNotNull(BulkActionRequestDto.FilterType.BY_AUTHOR);
        assertNotNull(BulkActionRequestDto.FilterType.ALL);
        assertEquals(4, BulkActionRequestDto.FilterType.values().length);
    }
}
