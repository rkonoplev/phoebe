package com.example.phoebe.mapper;

import com.example.phoebe.dto.HomepageModeDto;
import com.example.phoebe.entity.HomepageSettings;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HomepageSettingsMapper {
    HomepageModeDto toDto(HomepageSettings entity);
    HomepageSettings toEntity(HomepageModeDto dto);
}
