package com.example.phoebe.unit.controller;

import com.example.phoebe.controller.PublicChannelSettingsController;
import com.example.phoebe.dto.response.ChannelSettingsDto;
import com.example.phoebe.service.ChannelSettingsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicChannelSettingsControllerTest {

    @Mock
    private ChannelSettingsService channelSettingsService;

    @InjectMocks
    private PublicChannelSettingsController controller;

    @Test
    void getChannelSettingsShouldReturnPublicSettings() {
        ChannelSettingsDto settingsDto = new ChannelSettingsDto(
                "Public Channel",
                "Public Description",
                "keywords",
                "<header></header>",
                "http://logo.png",
                "<footer></footer>",
                "[]",
                "http://public.com"
        );
        when(channelSettingsService.getSettings()).thenReturn(settingsDto);

        ResponseEntity<ChannelSettingsDto> response = controller.getChannelSettings();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Public Channel", response.getBody().siteTitle());
    }
}
