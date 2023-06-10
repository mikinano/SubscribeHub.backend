package com.subscribehub.app.controller;

import com.subscribehub.app.dto.ArticleDto;
import com.subscribehub.app.dto.ArticleSearchCondition;
import com.subscribehub.app.dto.UserSiteDto;
import com.subscribehub.app.service.ArticleService;
import com.subscribehub.app.service.UserSiteService;
import com.subscribehub.app.service.crawler.CrawlerService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
@AllArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
    private final CrawlerService crawlerService;
    private final UserSiteService userSiteService;

    @GetMapping
    public Page<ArticleDto> searchArticle(ArticleSearchCondition condition, Pageable pageable, Principal principal) {
        return articleService.searchPagination(condition, pageable, principal.getName());
    }

    @PostMapping
    public String crawlArticles(Principal principal) throws Exception {
        List<UserSiteDto> siteDtoList = userSiteService.searchSiteDto(principal.getName());
        for (UserSiteDto userSiteDto : siteDtoList) {
            crawlerService.doCrawling(userSiteDto.getSiteFullUrl(), userSiteDto.getUser(), userSiteDto.getSite());
        }

        return "Test";
    }
}
