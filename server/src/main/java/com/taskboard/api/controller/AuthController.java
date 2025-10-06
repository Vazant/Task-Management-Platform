package com.taskboard.api.controller;

import com.taskboard.api.constants.AppConstants;
import com.taskboard.api.constants.HttpStatusConstants;
import com.taskboard.api.dto.ApiResponse;
import com.taskboard.api.dto.LoginRequest;
import com.taskboard.api.dto.LoginResponse;
import com.taskboard.api.dto.RegisterRequest;
import com.taskboard.api.service.MessageService;
import com.taskboard.user.service.UserService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AppConstants.API_AUTH_PATH)
@SuppressWarnings("checkstyle:DesignForExtension")
public class AuthController {

  private final UserService userService;
  private final MessageService messageService;

  public AuthController(final UserService userService, final MessageService messageService) {
    this.userService = userService;
    this.messageService = messageService;
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponse>> login(
      @Valid @RequestBody final LoginRequest request) {
    try {
      final LoginResponse response = userService.login(request);
      return ResponseEntity.ok(
          new ApiResponse<>(response, messageService.getMessage("auth.login.success"), true));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatusConstants.HTTP_UNAUTHORIZED).body(new ApiResponse<>(null, e.getMessage(), false));
    }
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<LoginResponse>> register(
      @Valid @RequestBody final RegisterRequest request) {
    final LoginResponse response = userService.register(request);
    return ResponseEntity.ok(
        new ApiResponse<>(response, messageService.getMessage("auth.register.success"), true));
  }

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<Map<String, String>>> refreshToken(
      @RequestBody final Map<String, String> request) {
    try {
      final String refreshToken = request.get("refreshToken");
      if (refreshToken == null || refreshToken.trim().isEmpty()) {
        return ResponseEntity.badRequest()
            .body(
                new ApiResponse<>(null, messageService.getMessage("auth.refresh.required"), false));
      }

      final String newToken =
          userService
              .refreshToken(new com.taskboard.user.dto.RefreshTokenRequest(refreshToken))
              .getToken();

      final Map<String, String> response = new HashMap<>();
      response.put("token", newToken);
      response.put("refreshToken", refreshToken);

      return ResponseEntity.ok(
          new ApiResponse<>(response, messageService.getMessage("auth.refresh.success"), true));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(null, e.getMessage(), false));
    }
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<ApiResponse<String>> forgotPassword(
      @RequestBody final Map<String, String> request) {
    try {
      final String email = request.get("email");
      if (email == null || email.trim().isEmpty()) {
        return ResponseEntity.badRequest()
            .body(
                new ApiResponse<>(
                    null, messageService.getMessage("validation.email.required"), false));
      }

      // TODO(@developer): Реализовать логику восстановления пароля
      // authService.forgotPassword(email);

      return ResponseEntity.ok(
          new ApiResponse<>(
              messageService.getMessage("auth.password.reset.sent"),
              messageService.getMessage("auth.password.reset.instruction"),
              true));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(null, e.getMessage(), false));
    }
  }

  @PostMapping("/reset-password")
  public ResponseEntity<ApiResponse<String>> resetPassword(
      @RequestBody final Map<String, String> request) {
    try {
      final String token = request.get("token");
      final String password = request.get("password");

      if (token == null || token.trim().isEmpty()) {
        return ResponseEntity.badRequest()
            .body(
                new ApiResponse<>(
                    null, messageService.getMessage("auth.password.reset.token_required"), false));
      }

      if (password == null || password.trim().isEmpty()) {
        return ResponseEntity.badRequest()
            .body(
                new ApiResponse<>(
                    null,
                    messageService.getMessage("auth.password.reset.password_required"),
                    false));
      }

      // TODO(@developer): Реализовать логику сброса пароля
      // authService.resetPassword(token, password);

      return ResponseEntity.ok(
          new ApiResponse<>(
              messageService.getMessage("auth.password.reset.success"),
              messageService.getMessage("auth.password.reset.success"),
              true));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new ApiResponse<>(null, e.getMessage(), false));
    }
  }
}
