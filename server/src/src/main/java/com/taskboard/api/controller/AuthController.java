package com.taskboard.api.controller;

import com.taskboard.api.dto.ApiResponse;
import com.taskboard.api.dto.LoginRequest;
import com.taskboard.api.dto.LoginResponse;
import com.taskboard.api.dto.RegisterRequest;
import com.taskboard.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(response, "Вход выполнен успешно", true));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ResponseEntity.ok(new ApiResponse<>(response, "Регистрация выполнена успешно", true));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(null, "Refresh token обязателен", false));
            }

            String newToken = authService.refreshToken(refreshToken);

            Map<String, String> response = new HashMap<>();
            response.put("token", newToken);
            response.put("refreshToken", refreshToken);

            return ResponseEntity.ok(new ApiResponse<>(response, "Токен обновлен", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, e.getMessage(), false));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(null, "Email обязателен", false));
            }

            // TODO: Реализовать логику восстановления пароля
            // authService.forgotPassword(email);

            return ResponseEntity.ok(new ApiResponse<>(
                    "Инструкции отправлены на email",
                    "Инструкции отправлены",
                    true));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, e.getMessage(), false));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            String password = request.get("password");

            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(null, "Token обязателен", false));
            }

            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(null, "Новый пароль обязателен", false));
            }

            // TODO: Реализовать логику сброса пароля
            // authService.resetPassword(token, password);

            return ResponseEntity.ok(new ApiResponse<>("Пароль изменен", "Пароль успешно изменен", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, e.getMessage(), false));
        }
    }
}
