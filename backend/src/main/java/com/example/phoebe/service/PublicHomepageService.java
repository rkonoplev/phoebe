package com.example.phoebe.service;

import com.example.phoebe.dto.response.PublicHomepageBlockDto;
import com.example.phoebe.dto.response.PublicHomepageResponseDto;
import com.example.phoebe.dto.response.PublicNewsDto;
import com.example.phoebe.entity.HomePageBlock;
import com.example.phoebe.entity.News;
import com.example.phoebe.model.HomePageBlockType;
import com.example.phoebe.model.HomepageMode;
import com.example.phoebe.repository.HomePageBlockRepository;
import com.example.phoebe.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicHomepageService {

    private final HomepageSettingsService settingsService;
    private final NewsRepository newsRepository;
    private final HomePageBlockRepository blockRepository;

    @Transactional(readOnly = true)
    public PublicHomepageResponseDto getHomepageContent() {
        HomepageMode mode = settingsService.getCurrentMode().getMode();
        PublicHomepageResponseDto response = new PublicHomepageResponseDto();
        response.setMode(mode);

        if (mode == HomepageMode.SIMPLE) {
            List<News> latestNews = newsRepository.findAll(PageRequest.of(0, 10, Sort.by("publishedAt").descending())).getContent();
            response.setNews(latestNews.stream().map(this::toPublicNewsDto).collect(Collectors.toList()));
        } else {
            List<HomePageBlock> blocks = blockRepository.findAll(Sort.by("weight"));
            response.setBlocks(blocks.stream().map(this::toPublicHomepageBlockDto).collect(Collectors.toList()));
        }

        return response;
    }

    private PublicHomepageBlockDto toPublicHomepageBlockDto(HomePageBlock block) {
        PublicHomepageBlockDto dto = new PublicHomepageBlockDto();
        dto.setId(block.getId());
        dto.setWeight(block.getWeight());
        dto.setBlockType(block.getBlockType());
        dto.setContent(block.getContent());
        dto.setShowTeaser(block.getShowTeaser());
        dto.setTitleFontSize(block.getTitleFontSize());

        if (block.getBlockType() == HomePageBlockType.NEWS_BLOCK) {
            List<News> news = newsRepository.findNewsByTaxonomyTerms(
                    block.getTaxonomyTerms().stream().map(term -> (long)term.getId()).collect(Collectors.toSet()),
                    PageRequest.of(0, block.getNewsCount())
            );
            dto.setNews(news.stream().map(this::toPublicNewsDto).collect(Collectors.toList()));
        }

        return dto;
    }

    private PublicNewsDto toPublicNewsDto(News news) {
        return new PublicNewsDto(
                news.getId(),
                news.getTitle(),
                news.getSlug(),
                news.getTeaser(),
                news.getPublishedAt()
        );
    }
}
