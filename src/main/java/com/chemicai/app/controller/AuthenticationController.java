package com.chemicai.app.controller;

import com.chemicai.app.dto.AuthenticationRequest;
import com.chemicai.app.dto.AuthenticationResponse;
import com.chemicai.app.dto.RefreshTokenDto;
import com.chemicai.app.dto.RegisterRequest;
import com.chemicai.app.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequest request
    ) {
        service.register(request);
        return ResponseEntity.ok("회원가입 성공");
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/new-access-token")
    public void newAccessToken(
            @RequestBody RefreshTokenDto refreshTokenDto,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(refreshTokenDto.getRefreshToken(), request, response);
    }
}