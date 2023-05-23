package com.subscribehub.app.service;

import com.subscribehub.app.config.jwt.TokenProvider;
import com.subscribehub.app.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenProvider tokenProvider;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken) {
        // 토큰 유효성 검사에 실패하면 예외 발생
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }

        User user = userService.findByRefreshToken(refreshToken);

        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
}