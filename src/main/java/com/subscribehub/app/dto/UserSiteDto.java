package com.subscribehub.app.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.subscribehub.app.domain.Site;
import com.subscribehub.app.domain.User;
import lombok.Data;

@Data
public class UserSiteDto {
    private String siteFullUrl;

    private Site site;

    private User user;

    @QueryProjection
    public UserSiteDto(String siteFullUrl, Site site, User user) {
        this.siteFullUrl = siteFullUrl;
        this.site = site;
        this.user = user;
    }
}
