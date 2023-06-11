package com.subscribehub.app.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.subscribehub.app.domain.Site;
import com.subscribehub.app.domain.User;
import lombok.Data;

@Data
public class UserSiteDto {
    private String url;

    private String nickname;

    @QueryProjection
    public UserSiteDto(String url, String nickname) {
        this.url = url;
        this.nickname = nickname;
    }
}
