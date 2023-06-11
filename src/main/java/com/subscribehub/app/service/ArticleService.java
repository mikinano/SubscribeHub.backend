package com.subscribehub.app.service;

import com.subscribehub.app.domain.Article;
import com.subscribehub.app.domain.UserSite;
import com.subscribehub.app.dto.ArticleDto;
import com.subscribehub.app.dto.ArticleSearchCondition;
import com.subscribehub.app.repository.ArticleAdvancedRepository;
import com.subscribehub.app.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleService {
    private final ArticleAdvancedRepository articleAdvancedRepository;
    private final KeywordService keywordService;

    public Page<ArticleDto> searchPagination(Long siteId, Pageable pageable, String userEmail, List<String> keywordList) {
        return articleAdvancedRepository.searchPagination(userEmail, siteId, pageable, keywordList);
    }
}
