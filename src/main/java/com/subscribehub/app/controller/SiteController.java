package com.subscribehub.app.controller;

import com.subscribehub.app.domain.User;
import com.subscribehub.app.dto.AddUserSiteRequest;
import com.subscribehub.app.dto.SiteDto;
import com.subscribehub.app.dto.UserSiteDto;
import com.subscribehub.app.service.SiteService;
import com.subscribehub.app.service.UserService;
import com.subscribehub.app.service.UserSiteService;
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

    @GetMapping
    public List<SiteDto> siteList() {
        return siteService.siteList().stream().map((site) -> new SiteDto(site.getId(), site.getUrl(), site.getSiteNickname())).toList();
    }

    @GetMapping("/userSites")
    public List<UserSiteDto> userSiteList(Principal principal) {
        return userSiteService.userSiteList(principal.getName()).stream().map((userSite) -> new UserSiteDto(userSite.getUrl(), userSite.getNickname())).toList();
    }

    @PutMapping
    public ResponseEntity<String> addUserSite(@RequestBody List<AddUserSiteRequest> request, Principal principal) {
        User user = userService.findOneByEmail(principal.getName());
        userSiteService.putUserSite(user, request);

        return ResponseEntity.ok("회원가입 성공");
    }
}
