package com.subscribehub.app.controller;

import com.subscribehub.app.domain.UserSite;
import com.subscribehub.app.dto.ArticleDto;
import com.subscribehub.app.dto.ArticleResponseDto;
import com.subscribehub.app.service.ArticleService;
import com.subscribehub.app.service.UserSiteService;
import com.subscribehub.app.service.crawler.CrawlerService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
@AllArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
    private final CrawlerService crawlerService;
    private final UserSiteService userSiteService;

    @GetMapping
    public ArticleResponseDto searchArticle(@RequestParam Long siteId, Pageable pageable, Principal principal) throws Exception {
        List<UserSite> userSiteList = userSiteService.userSiteList(principal.getName());
        List<ArticleDto> updatedList = new ArrayList<>();
        for (UserSite userSite : userSiteList) {
            crawlerService.doCrawling(userSite.getUser(), userSite, updatedList);
        }

        Page<ArticleDto> pagingResult = articleService.searchPagination(siteId, pageable, principal.getName());

        return new ArticleResponseDto(pagingResult, updatedList);
    }

    @GetMapping("/id/{articleId}")
    public String getArticleContent(@PathVariable("articleId") Long articleId) {
        return crawlerService.getArticleContent(articleId);
    }
}
