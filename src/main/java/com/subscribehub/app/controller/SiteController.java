package com.subscribehub.app.controller;

import com.subscribehub.app.domain.User;
import com.subscribehub.app.dto.AddUserSiteRequest;
import com.subscribehub.app.dto.SiteDto;
import com.subscribehub.app.dto.UserSiteDto;
import com.subscribehub.app.service.SiteService;
import com.subscribehub.app.service.UserService;
import com.subscribehub.app.service.UserSiteService;
import com.subscribehub.app.service.crawler.CrawlerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/sites")
@AllArgsConstructor
public class SiteController {
    private final SiteService siteService;
    private final UserSiteService userSiteService;
    private final UserService userService;
    private final CrawlerService crawlerService;

    @GetMapping
    public List<SiteDto> siteList() {
        return siteService.siteList().stream().map((site) -> new SiteDto(site.getId(), site.getUrl(), site.getSiteNickname())).toList();
    }

    @GetMapping("/user-sites")
    public List<UserSiteDto> userSiteList(Principal principal) {
        return userSiteService.userSiteList(principal.getName()).stream().map((userSite) -> new UserSiteDto(userSite.getUrl(), userSite.getNickname())).toList();
    }

    @PutMapping
    public ResponseEntity<String> addUserSite(@RequestBody List<AddUserSiteRequest> request, Principal principal) {
        User user = userService.findOneByEmail(principal.getName());
        userSiteService.putUserSite(user, request);

        return ResponseEntity.ok("사이트 등록 성공");
    }

    @GetMapping("/search-user-sites")
    public List<UserSiteDto> searchUserSites(@RequestParam String searchWord, @RequestParam Long siteId) throws Exception {
        return crawlerService.searchUserSites(searchWord, siteId);
    }
}
