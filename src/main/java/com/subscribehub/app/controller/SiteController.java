package com.subscribehub.app.controller;

import com.subscribehub.app.domain.Site;
import com.subscribehub.app.domain.User;
import com.subscribehub.app.domain.UserSite;
import com.subscribehub.app.dto.AddUserSiteRequest;
import com.subscribehub.app.service.SiteService;
import com.subscribehub.app.service.UserService;
import com.subscribehub.app.service.UserSiteService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/sites")
@AllArgsConstructor
public class SiteController {
    private final SiteService siteService;
    private final UserSiteService userSiteService;
    private final UserService userService;

    @PostMapping
    public String addUserSite(@RequestBody AddUserSiteRequest addUserSiteRequest, Principal principal) {
        System.out.println("addUserSiteRequest = " + addUserSiteRequest);
        User user = userService.findOneByEmail(principal.getName());
        Site site = siteService.findOneById(addUserSiteRequest.getSiteId());
        System.out.println(addUserSiteRequest.getPostUrl());
        System.out.println(addUserSiteRequest.getNickname());
        userSiteService.save(new UserSite(user, site, addUserSiteRequest.getPostUrl(), addUserSiteRequest.getNickname()));

        return "test";
    }
}
