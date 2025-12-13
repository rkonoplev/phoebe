package com.example.phoebe.service;

import com.example.phoebe.dto.HomePageBlockDto;
import com.example.phoebe.entity.HomePageBlock;
import com.example.phoebe.entity.TaxonomyTerm;
import com.example.phoebe.mapper.HomePageBlockMapper;
import com.example.phoebe.repository.HomePageBlockRepository;
import com.example.phoebe.repository.TaxonomyTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomePageBlockService {

    private final HomePageBlockRepository blockRepository;
    private final TaxonomyTermRepository termRepository;
    private final HomePageBlockMapper blockMapper;

    @Transactional(readOnly = true)
    public List<HomePageBlockDto> findAll() {
        return blockRepository.findAll().stream()
                .map(blockMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HomePageBlockDto findById(Integer id) {
        return blockRepository.findById(id)
                .map(blockMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Block not found")); // Replace with specific exception
    }

    @Transactional
    public HomePageBlockDto create(HomePageBlockDto dto) {
        HomePageBlock block = blockMapper.toEntity(dto);
        updateTaxonomyTerms(block, dto.getTaxonomyTermIds());
        HomePageBlock savedBlock = blockRepository.save(block);
        return blockMapper.toDto(savedBlock);
    }

    @Transactional
    public HomePageBlockDto update(Integer id, HomePageBlockDto dto) {
        HomePageBlock block = blockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Block not found"));

        // Update fields from DTO
        block.setWeight(dto.getWeight());
        block.setBlockType(dto.getBlockType());
        block.setNewsCount(dto.getNewsCount());
        block.setShowTeaser(dto.getShowTeaser());
        block.setTitleFontSize(dto.getTitleFontSize());
        block.setContent(dto.getContent());

        updateTaxonomyTerms(block, dto.getTaxonomyTermIds());
        HomePageBlock updatedBlock = blockRepository.save(block);
        return blockMapper.toDto(updatedBlock);
    }



    public void delete(Integer id) {
        blockRepository.deleteById(id);
    }

    private void updateTaxonomyTerms(HomePageBlock block, Set<Integer> termIds) {
        if (termIds == null || termIds.isEmpty()) {
            block.setTaxonomyTerms(new HashSet<>());
        } else {
            Set<TaxonomyTerm> terms = new HashSet<>(termRepository.findAllById(termIds));
            block.setTaxonomyTerms(terms);
        }
    }
}
