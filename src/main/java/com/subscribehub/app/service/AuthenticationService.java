package com.subscribehub.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscribehub.app.config.JwtService;
import com.subscribehub.app.domain.Role;
import com.subscribehub.app.domain.User;
import com.subscribehub.app.dto.AuthenticationRequest;
import com.subscribehub.app.dto.AuthenticationResponse;
import com.subscribehub.app.dto.RegisterRequest;
import com.subscribehub.app.global.ErrorResponse;
import com.subscribehub.app.global.exception.DuplicateUserException;
import com.subscribehub.app.repository.UserRepository;
import com.subscribehub.app.token.Token;
import com.subscribehub.app.token.TokenRepository;
import com.subscribehub.app.token.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public void register(RegisterRequest request) {
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateUserException();
        }
        repository.save(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveRefreshToken(user, refreshToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveRefreshToken(User user, String refreshToken) {
        Optional<Token> findToken = tokenRepository.findByUser(user);
        if (findToken.isPresent()) {
            findToken.get().setToken(refreshToken);
        } else {
            var token = Token.builder()
                    .user(user)
                    .token(refreshToken)
                    .tokenType(TokenType.BEARER)
                    .build();
            tokenRepository.save(token);
        }
    }

    public void refreshToken(
            String refreshToken,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String userEmail;
        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            return;
        }
        refreshToken = refreshToken.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            Token userRefreshToken = tokenRepository.findByUser(user).orElse(null);
            if (jwtService.extractTokenType(refreshToken).equals("refresh") && jwtService.isTokenValid(refreshToken, user)
                    && userRefreshToken != null && userRefreshToken.getToken().equals(refreshToken)) {
                var accessToken = jwtService.generateToken(user);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            } else {
                response.setStatus(SC_BAD_REQUEST);
                response.setContentType(APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("utf-8");
                ErrorResponse errorResponse = new ErrorResponse(400, "유효하지 않은 JWT 토큰입니다.");
                new ObjectMapper().writeValue(response.getWriter(), errorResponse);
            }
        }
    }
}