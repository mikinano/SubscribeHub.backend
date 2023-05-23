package com.subscribehub.app.controller;

import com.subscribehub.app.dto.CreateAccessTokenRequest;
import com.subscribehub.app.dto.CreateAccessTokenResponse;
import com.subscribehub.app.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenApiController {
    private final TokenService tokenService;

    @PostMapping("/api/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken
            (@RequestBody CreateAccessTokenRequest tokenRequest,
             HttpServletRequest request,
             HttpServletResponse response) {
        try {
            String newAccessToken = tokenService.createNewAccessToken(tokenRequest.getRefreshToken());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CreateAccessTokenResponse(newAccessToken));
        } catch (IllegalArgumentException e) {
            new SecurityContextLogoutHandler().logout(request, response,
                    SecurityContextHolder.getContext().getAuthentication());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }
    }
}
