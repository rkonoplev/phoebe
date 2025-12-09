package com.example.phoebe.unit.service;

import com.example.phoebe.dto.request.ChannelSettingsUpdateDto;
import com.example.phoebe.dto.response.ChannelSettingsDto;
import com.example.phoebe.entity.ChannelSettings;
import com.example.phoebe.mapper.ChannelSettingsMapper;
import com.example.phoebe.repository.ChannelSettingsRepository;
import com.example.phoebe.service.impl.ChannelSettingsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChannelSettingsServiceTest {

    @Mock
    private ChannelSettingsRepository repository;

    @Mock
    private ChannelSettingsMapper mapper;

    @InjectMocks
    private ChannelSettingsServiceImpl service;

    private ChannelSettings settings;
    private ChannelSettingsDto settingsDto;

    @BeforeEach
    void setUp() {
        settings = new ChannelSettings();
        settings.setId(1L);
        settings.setSiteTitle("Test Site");

        settingsDto = new ChannelSettingsDto(
                "Test Site",
                "Description",
                "keywords",
                "<header></header>",
                "/logo.png",
                "<footer></footer>",
                "[]",
                "http://test.com"
        );
    }

    @Test
    void getSettingsExistingSettingsReturnsDto() {
        when(repository.findSingletonSettings()).thenReturn(Optional.of(settings));
        when(mapper.toDto(settings)).thenReturn(settingsDto);

        ChannelSettingsDto result = service.getSettings();

        assertNotNull(result);
        assertEquals("Test Site", result.siteTitle());
        verify(repository).findSingletonSettings();
    }

    @Test
    void getSettingsNoSettingsCreatesDefault() {
        when(repository.findSingletonSettings()).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(settings);
        when(mapper.toDto(any())).thenReturn(settingsDto);

        ChannelSettingsDto result = service.getSettings();

        assertNotNull(result);
        verify(repository).save(any(ChannelSettings.class));
    }

    @Test
    void updateSettingsUpdatesAndReturnsDto() {
        ChannelSettingsUpdateDto updateDto = new ChannelSettingsUpdateDto(
                "Updated Site",
                "Updated Description",
                "new keywords",
                "<header></header>",
                "/new-logo.png",
                "<footer></footer>",
                "[]",
                "http://updated.com"
        );
        when(repository.findSingletonSettings()).thenReturn(Optional.of(settings));
        when(repository.save(any())).thenReturn(settings);
        when(mapper.toDto(any())).thenReturn(settingsDto);

        ChannelSettingsDto result = service.updateSettings(updateDto);

        assertNotNull(result);
        verify(mapper).updateEntity(eq(settings), eq(updateDto));
        verify(repository).save(settings);
    }
}
