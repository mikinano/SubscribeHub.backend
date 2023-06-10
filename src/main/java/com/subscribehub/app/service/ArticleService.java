package com.subscribehub.app.service;

import com.subscribehub.app.dto.ArticleDto;
import com.subscribehub.app.dto.ArticleSearchCondition;
import com.subscribehub.app.repository.ArticleAdvancedRepository;
import com.subscribehub.app.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleService {
    private final ArticleAdvancedRepository articleAdvancedRepository;
    private final KeywordRepository keywordRepository;

    public Page<ArticleDto> searchPagination(ArticleSearchCondition condition, Pageable pageable, String userEmail) {
        condition.setKeywordList(keywordRepository.findKeywordListByUser(userEmail));
        return articleAdvancedRepository.searchPagination(condition, pageable);
    }
}
