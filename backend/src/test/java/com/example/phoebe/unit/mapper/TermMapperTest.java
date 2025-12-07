package com.example.phoebe.unit.mapper;

import com.example.phoebe.dto.request.TermCreateRequestDto;
import com.example.phoebe.dto.request.TermUpdateRequestDto;
import com.example.phoebe.dto.response.TermResponseDto;
import com.example.phoebe.entity.Term;
import com.example.phoebe.mapper.TermMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class TermMapperTest {

    private final TermMapper mapper = Mappers.getMapper(TermMapper.class);

    @Test
    void toResponse_ShouldMapEntityToDto() {
        Term term = new Term("Category", "category");
        term.setId(1L);

        TermResponseDto dto = mapper.toResponse(term);

        assertEquals(1L, dto.id());
        assertEquals("Category", dto.name());
        assertEquals("category", dto.vocabulary());
    }

    @Test
    void toEntity_ShouldCreateNewEntity() {
        TermCreateRequestDto dto = new TermCreateRequestDto("Tag", "tag");

        Term entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertNull(entity.getId()); // ID should not be set
    }
}
