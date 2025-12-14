package com.example.phoebe.mapper;

import com.example.phoebe.dto.HomepageModeDto;
import com.example.phoebe.entity.HomepageSettings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HomepageSettingsMapper {
    HomepageModeDto toDto(HomepageSettings entity);

    @Mapping(target = "id", ignore = true)
    HomepageSettings toEntity(HomepageModeDto dto);
}
