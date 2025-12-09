package com.example.phoebe.unit.mapper;

import com.example.phoebe.dto.request.ChannelSettingsUpdateDto;
import com.example.phoebe.dto.response.ChannelSettingsDto;
import com.example.phoebe.entity.ChannelSettings;
import com.example.phoebe.mapper.ChannelSettingsMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChannelSettingsMapperTest {

    private final ChannelSettingsMapper mapper = Mappers.getMapper(ChannelSettingsMapper.class);

    @Test
    void toDtoShouldMapEntityToDto() {
        ChannelSettings entity = new ChannelSettings();
        entity.setId(1L);
        entity.setSiteTitle("Test Site");
        entity.setMetaDescription("Description");
        entity.setMetaKeywords("keywords");
        entity.setHeaderHtml("<header></header>");
        entity.setLogoUrl("/logo.png");
        entity.setFooterHtml("<footer></footer>");
        entity.setMainMenuTermIds("[]");
        entity.setSiteUrl("http://test.com");

        ChannelSettingsDto dto = mapper.toDto(entity);

        assertEquals("Test Site", dto.siteTitle());
        assertEquals("Description", dto.metaDescription());
        assertEquals("keywords", dto.metaKeywords());
        assertEquals("<header></header>", dto.headerHtml());
        assertEquals("/logo.png", dto.logoUrl());
        assertEquals("<footer></footer>", dto.footerHtml());
        assertEquals("[]", dto.mainMenuTermIds());
        assertEquals("http://test.com", dto.siteUrl());
    }

    @Test
    void updateEntityShouldUpdateNonNullFields() {
        ChannelSettings entity = new ChannelSettings();
        entity.setId(1L);
        entity.setSiteTitle("Old Title");
        entity.setMetaDescription("Old Description");

        ChannelSettingsUpdateDto dto = new ChannelSettingsUpdateDto(
                "New Title",
                null, // Should not update
                "new keywords",
                null,
                null,
                null,
                null,
                null
        );

        mapper.updateEntity(entity, dto);

        assertEquals("New Title", entity.getSiteTitle());
        assertEquals("Old Description", entity.getMetaDescription()); // Not updated
        assertEquals("new keywords", entity.getMetaKeywords());
        assertEquals(1L, entity.getId()); // ID should not change
    }
}
