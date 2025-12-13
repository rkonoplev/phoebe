package com.example.phoebe.mapper;

import com.example.phoebe.dto.HomePageBlockDto;
import com.example.phoebe.entity.HomePageBlock;
import com.example.phoebe.entity.TaxonomyTerm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface HomePageBlockMapper {

    @Mapping(source = "taxonomyTerms", target = "taxonomyTermIds", qualifiedByName = "termsToIds")
    HomePageBlockDto toDto(HomePageBlock entity);

    @Mapping(target = "taxonomyTerms", ignore = true) // Handled in service layer
    HomePageBlock toEntity(HomePageBlockDto dto);

    @Named("termsToIds")
    default Set<Integer> termsToIds(Set<TaxonomyTerm> terms) {
        if (terms == null) {
            return Collections.emptySet();
        }
        return terms.stream().map(TaxonomyTerm::getId).collect(Collectors.toSet());
    }
}
