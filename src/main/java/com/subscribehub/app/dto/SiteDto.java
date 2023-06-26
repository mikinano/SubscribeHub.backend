package com.subscribehub.app.dto;

import lombok.Data;

@Data
public class SiteDto {
    private Long id;
    private String url;
    private String siteNickname;

    public SiteDto(Long id, String url, String siteNickname) {
        this.id = id;
        this.url = url;
        this.siteNickname = siteNickname;
    }
}
