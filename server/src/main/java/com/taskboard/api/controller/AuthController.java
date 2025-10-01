package com.taskboard.api.controller;

import com.taskboard.api.dto.ApiResponse;
import com.taskboard.api.dto.LoginRequest;
import com.taskboard.api.dto.LoginResponse;
import com.taskboard.api.dto.RegisterRequest;
import com.taskboard.api.service.AuthService;
import com.taskboard.api.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final MessageService messageService;

    public AuthController(AuthService authService, MessageService messageService) {
        this.authService = authService;
        this.messageService = messageService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(new ApiResponse<>(response, messageService.getMessage("auth.login.success"), true));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(null, e.getMessage(), false));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ResponseEntity.ok(new ApiResponse<>(response, messageService.getMessage("auth.register.success"), true));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(null, messageService.getMessage("auth.refresh.required"), false));
            }

            String newToken = authService.refreshToken(refreshToken);

            Map<String, String> response = new HashMap<>();
            response.put("token", newToken);
            response.put("refreshToken", refreshToken);

            return ResponseEntity.ok(new ApiResponse<>(response, messageService.getMessage("auth.refresh.success"), true));
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
                        .body(new ApiResponse<>(null, messageService.getMessage("validation.email.required"), false));
            }

            // TODO: Реализовать логику восстановления пароля
            // authService.forgotPassword(email);

            return ResponseEntity.ok(new ApiResponse<>(
                    messageService.getMessage("auth.password.reset.sent"),
                    messageService.getMessage("auth.password.reset.instruction"),
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
                        .body(new ApiResponse<>(null, messageService.getMessage("auth.password.reset.token_required"), false));
            }

            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(null, messageService.getMessage("auth.password.reset.password_required"), false));
            }

            // TODO: Реализовать логику сброса пароля
            // authService.resetPassword(token, password);

            return ResponseEntity.ok(new ApiResponse<>(
                    messageService.getMessage("auth.password.reset.success"), 
                    messageService.getMessage("auth.password.reset.success"), 
                    true));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, e.getMessage(), false));
        }
    }
}
