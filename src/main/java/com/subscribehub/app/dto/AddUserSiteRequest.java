package com.subscribehub.app.dto;

import lombok.Data;

@Data
public class AddUserSiteRequest {
    private Long siteId;

    private String url;

    private String nickname;
}
