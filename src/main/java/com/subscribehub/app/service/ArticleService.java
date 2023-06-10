package com.subscribehub.app.service;

import com.subscribehub.app.dto.ArticleDto;
import com.subscribehub.app.dto.ArticleSearchCondition;
import com.subscribehub.app.repository.ArticleAdvancedRepository;
import com.subscribehub.app.repository.ArticleRepository;
import com.subscribehub.app.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleAdvancedRepository articleAdvancedRepository;
    private final KeywordRepository keywordRepository;

    public Page<ArticleDto> searchPagination(ArticleSearchCondition condition, Pageable pageable, String userEmail) {
        List<String> keywordList = keywordRepository.findKeywordListByUser(userEmail);
        return articleAdvancedRepository.searchPagination(condition, pageable);
    }
}
