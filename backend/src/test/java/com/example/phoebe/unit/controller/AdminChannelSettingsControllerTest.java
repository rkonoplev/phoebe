package com.example.phoebe.unit.controller;

import com.example.phoebe.controller.AdminChannelSettingsController;
import com.example.phoebe.dto.request.ChannelSettingsUpdateDto;
import com.example.phoebe.dto.response.ChannelSettingsDto;
import com.example.phoebe.service.ChannelSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminChannelSettingsControllerTest {

    @Mock
    private ChannelSettingsService channelSettingsService;

    @InjectMocks
    private AdminChannelSettingsController controller;

    private ChannelSettingsDto settingsDto;

    @BeforeEach
    void setUp() {
        settingsDto = new ChannelSettingsDto(
                "Test Channel",
                "Test Description",
                "test, keywords",
                "<header></header>",
                "http://test.com/logo.png",
                "<footer></footer>",
                "[]",
                "http://test.com"
        );
    }

    @Test
    void getChannelSettingsShouldReturnSettings() {
        when(channelSettingsService.getSettings()).thenReturn(settingsDto);

        ResponseEntity<ChannelSettingsDto> response = controller.getChannelSettings();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Channel", response.getBody().siteTitle());
    }

    @Test
    void updateChannelSettingsShouldReturnUpdatedSettings() {
        ChannelSettingsUpdateDto updateDto = new ChannelSettingsUpdateDto(
                "Updated Channel",
                "Updated Description",
                "keywords",
                "<header></header>",
                "http://logo.png",
                "<footer></footer>",
                "[]",
                "http://updated.com"
        );
        when(channelSettingsService.updateSettings(any())).thenReturn(settingsDto);

        ResponseEntity<ChannelSettingsDto> response = controller.updateChannelSettings(updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Channel", response.getBody().siteTitle());
    }
}
