package com.subscribehub.app.dto;

import lombok.Data;

@Data
public class RefreshTokenDto {
    private String refreshToken;

    public RefreshTokenDto(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
