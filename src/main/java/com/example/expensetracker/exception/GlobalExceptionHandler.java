package com.example.expensetracker.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Format JSON chung
    private ResponseEntity<Object> buildResponse(
            String error,
            String message,
            HttpStatus status,
            HttpServletRequest req,
            List<String> details) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("error", error);
        body.put("message", message);
        body.put("details", details);
        body.put("timestamp", Instant.now().toEpochMilli());
        body.put("path", req.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    // 1) DTO validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .toList();
        return buildResponse("VALIDATION_ERROR", "Invalid input data", HttpStatus.BAD_REQUEST, req, details);
    }

    // 2) Login sai password
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
        return buildResponse("BAD_CREDENTIALS", "Invalid email or password",
                HttpStatus.UNAUTHORIZED, req, List.of());
    }

    // 3) User không tồn tại
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFound(UsernameNotFoundException ex, HttpServletRequest req) {
        return buildResponse("USER_NOT_FOUND", ex.getMessage(),
                HttpStatus.NOT_FOUND, req, List.of());
    }

    // 4) Email đã tồn tại, hoặc lỗi logic khác
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArg(IllegalArgumentException ex, HttpServletRequest req) {
        return buildResponse("INVALID_ARGUMENT", ex.getMessage(),
                HttpStatus.BAD_REQUEST, req, List.of());
    }

    // 5) JWT hết hạn
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Object> handleExpiredJwt(ExpiredJwtException ex, HttpServletRequest req) {
        return buildResponse("JWT_EXPIRED", "Token expired, please login again",
                HttpStatus.UNAUTHORIZED, req, List.of());
    }

    // 6) JWT sai, không parse được
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Object> handleJwt(JwtException ex, HttpServletRequest req) {
        return buildResponse("JWT_INVALID", "Invalid token",
                HttpStatus.UNAUTHORIZED, req, List.of());
    }

    // 7) fallback cho mọi lỗi khác
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex, HttpServletRequest req) {
        return buildResponse("INTERNAL_ERROR", ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR, req, List.of());
    }
}
