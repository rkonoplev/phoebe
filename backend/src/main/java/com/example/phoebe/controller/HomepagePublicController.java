package com.example.phoebe.controller;

import com.example.phoebe.dto.HomepageModeDto;
import com.example.phoebe.dto.response.PublicHomepageResponseDto;
import com.example.phoebe.service.HomepageSettingsService;
import com.example.phoebe.service.PublicHomepageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/homepage")
public class HomepagePublicController {

    private final HomepageSettingsService settingsService;
    private final PublicHomepageService publicHomepageService;

    public HomepagePublicController(HomepageSettingsService settingsService, PublicHomepageService publicHomepageService) {
        this.settingsService = settingsService;
        this.publicHomepageService = publicHomepageService;
    }

    @GetMapping
    public ResponseEntity<PublicHomepageResponseDto> getHomepage() {
        return ResponseEntity.ok(publicHomepageService.getHomepageContent());
    }

    @GetMapping("/mode")
    public ResponseEntity<HomepageModeDto> getMode() {
        return ResponseEntity.ok(settingsService.getCurrentMode());
    }

    @PatchMapping("/mode")
    public ResponseEntity<HomepageModeDto> updateMode(@RequestBody HomepageModeDto modeDto) {
        return ResponseEntity.ok(settingsService.updateMode(modeDto));
    }
}
