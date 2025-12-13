package com.example.phoebe.service;

import com.example.phoebe.dto.HomepageModeDto;
import com.example.phoebe.entity.HomepageSettings;
import com.example.phoebe.mapper.HomepageSettingsMapper;
import com.example.phoebe.model.HomepageMode;
import com.example.phoebe.repository.HomepageSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomepageSettingsService {

    private final HomepageSettingsRepository settingsRepository;
    private final HomepageSettingsMapper settingsMapper;

    @Transactional(readOnly = true)
    public HomepageModeDto getCurrentMode() {
        // Assuming there's always one and only one settings entry
        return settingsRepository.findAll().stream().findFirst()
                .map(settingsMapper::toDto)
                .orElse(new HomepageModeDto(HomepageMode.SIMPLE)); // Default fallback
    }

    @Transactional
    public HomepageModeDto updateMode(HomepageModeDto modeDto) {
        HomepageSettings settings = settingsRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Settings not found")); // Should not happen

        settings.setMode(modeDto.getMode());
        HomepageSettings updatedSettings = settingsRepository.save(settings);
        return settingsMapper.toDto(updatedSettings);
    }
}
