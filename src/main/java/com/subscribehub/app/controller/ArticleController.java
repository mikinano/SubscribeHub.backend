package com.subscribehub.app.controller;

import com.subscribehub.app.domain.UserSite;
import com.subscribehub.app.dto.ArticleDto;
import com.subscribehub.app.dto.ArticleResponseDto;
import com.subscribehub.app.service.ArticleService;
import com.subscribehub.app.service.KeywordService;
import com.subscribehub.app.service.UserSiteService;
import com.subscribehub.app.service.crawler.CrawlerService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
@AllArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
    private final CrawlerService crawlerService;
    private final UserSiteService userSiteService;
    private final KeywordService keywordService;

    @GetMapping
    public List<ArticleDto> searchArticle(
            @RequestParam(required = false) Long siteId,
            @RequestParam(name = "keyword", required = false) List<String> keywordList,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Principal principal) throws Exception {
        List<UserSite> userSiteList = userSiteService.userSiteList(principal.getName());

        for (UserSite userSite : userSiteList) {
            crawlerService.doCrawling(userSite.getUser(), userSite);
        }

        return articleService.searchPagination(siteId, principal.getName(), keywordList, startDate, endDate);
    }

    @GetMapping("/id/{articleId}")
    public String getArticleContent(@PathVariable("articleId") Long articleId) throws Exception {
        return crawlerService.getArticleContent(articleId);
    }
}
