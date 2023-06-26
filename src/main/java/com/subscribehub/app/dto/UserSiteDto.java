package com.subscribehub.app.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.subscribehub.app.domain.Site;
import com.subscribehub.app.domain.User;
import lombok.Data;

@Data
public class UserSiteDto {
    private Long siteId;

    private String url;

    private String nickname;

    @QueryProjection
    public UserSiteDto(Long siteId, String url, String nickname) {
        this.siteId = siteId;
        this.url = url;
        this.nickname = nickname;
    }
}
