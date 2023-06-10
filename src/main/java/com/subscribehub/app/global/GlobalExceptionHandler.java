package com.subscribehub.app.global;

import com.subscribehub.app.global.exception.DuplicateUserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 로그인 실패
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> authenticationException() {
        ErrorResponse errorResponse = new ErrorResponse(401, "ID 또는 비밀번호가 일치하지 않습니다.");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // 중복된 유저
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorResponse> duplicateUser() {
        ErrorResponse errorResponse = new ErrorResponse(400, "이미 있는 계정입니다.");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
