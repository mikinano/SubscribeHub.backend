package com.chemicai.app.service;

import com.chemicai.app.global.exception.BlankRequestException;
import com.chemicai.app.global.exception.DuplicateUserException;
import com.chemicai.app.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.chemicai.app.config.JwtService;
import com.chemicai.app.domain.Role;
import com.chemicai.app.domain.User;
import com.chemicai.app.dto.AuthenticationRequest;
import com.chemicai.app.dto.AuthenticationResponse;
import com.chemicai.app.dto.RegisterRequest;
import com.chemicai.app.global.ErrorResponse;
import com.chemicai.app.token.Token;
import com.chemicai.app.token.TokenRepository;
import com.chemicai.app.token.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

import static io.micrometer.common.util.StringUtils.isBlank;
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
        if (isBlank(request.getEmail()) || isBlank(request.getPassword())) {
            throw new BlankRequestException();
        }
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
        if (refreshToken == null) {
            return;
        }

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