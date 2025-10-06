package com.taskboard.api.controller;

import com.taskboard.api.service.WebAuthnService;
import com.taskboard.user.model.WebAuthnCredential;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for WebAuthn (Passkeys) authentication Provides REST API endpoints for passwordless
 * authentication
 */
@RestController
@RequestMapping("/api/webauthn")
@Slf4j
public class WebAuthnController {

  @Autowired private WebAuthnService webAuthnService;

  /** Создает challenge для регистрации нового Passkey */
  @PostMapping("/register/challenge")
  public ResponseEntity<String> createRegistrationChallenge(
      @RequestParam String username, Authentication authentication) {

    try {
      // Получаем ID пользователя из аутентификации
      String userId = getUserIdFromAuthentication(authentication);

      String challenge = webAuthnService.createRegistrationChallenge(userId);

      log.info("Создан challenge для регистрации Passkey пользователя: {}", userId);
      return ResponseEntity.ok(challenge);

    } catch (Exception e) {
      log.error("Ошибка создания challenge для регистрации: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().body("Не удалось создать challenge для регистрации");
    }
  }

  /** Валидирует и сохраняет новый Passkey */
  @PostMapping("/register/verify")
  public ResponseEntity<Map<String, Object>> verifyRegistration(
      @RequestParam String challenge,
      @RequestParam String credentialResponse,
      Authentication authentication) {

    try {
      String userId = getUserIdFromAuthentication(authentication);

      boolean success =
          webAuthnService.validateAndSaveCredential(challenge, credentialResponse, userId);

      if (success) {
        log.info("Успешно зарегистрирован Passkey для пользователя: {}", userId);
        return ResponseEntity.ok(createSuccessResponse("Passkey успешно зарегистрирован"));
      } else {
        return ResponseEntity.badRequest()
            .body(createErrorResponse("Не удалось зарегистрировать Passkey"));
      }

    } catch (Exception e) {
      log.error("Ошибка регистрации Passkey: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().body(createErrorResponse("Ошибка регистрации Passkey"));
    }
  }

  /** Создает challenge для аутентификации с Passkey */
  @PostMapping("/authenticate/challenge")
  public ResponseEntity<String> createAuthenticationChallenge(@RequestParam String userId) {

    try {
      String challenge = webAuthnService.createAuthenticationChallenge(userId);

      log.info("Создан challenge для аутентификации пользователя: {}", userId);
      return ResponseEntity.ok(challenge);

    } catch (Exception e) {
      log.error("Ошибка создания challenge для аутентификации: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().body("Не удалось создать challenge для аутентификации");
    }
  }

  /** Валидирует аутентификацию с Passkey */
  @PostMapping("/authenticate/verify")
  public ResponseEntity<Map<String, Object>> verifyAuthentication(
      @RequestParam String challenge,
      @RequestParam String credentialResponse,
      @RequestParam String userId) {

    try {
      boolean success =
          webAuthnService.validateAuthentication(challenge, credentialResponse, userId);

      if (success) {
        log.info("Успешная аутентификация с Passkey для пользователя: {}", userId);
        return ResponseEntity.ok(createSuccessResponse("Аутентификация успешна"));
      } else {
        return ResponseEntity.badRequest().body(createErrorResponse("Аутентификация не удалась"));
      }

    } catch (Exception e) {
      log.error("Ошибка аутентификации с Passkey: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().body(createErrorResponse("Ошибка аутентификации"));
    }
  }

  /** Получает список Passkeys пользователя */
  @GetMapping("/credentials")
  public ResponseEntity<List<WebAuthnCredential>> getUserCredentials(
      Authentication authentication) {
    try {
      String userId = getUserIdFromAuthentication(authentication);
      List<WebAuthnCredential> credentials = webAuthnService.getUserCredentials(userId);

      return ResponseEntity.ok(credentials);

    } catch (Exception e) {
      log.error("Ошибка получения Passkeys: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().build();
    }
  }

  /** Удаляет Passkey */
  @DeleteMapping("/credentials/{credentialId}")
  public ResponseEntity<Map<String, Object>> deleteCredential(
      @PathVariable String credentialId, Authentication authentication) {

    try {
      String userId = getUserIdFromAuthentication(authentication);
      boolean success = webAuthnService.deleteCredential(credentialId, userId);

      if (success) {
        return ResponseEntity.ok(createSuccessResponse("Passkey успешно удален"));
      } else {
        return ResponseEntity.badRequest().body(createErrorResponse("Не удалось удалить Passkey"));
      }

    } catch (Exception e) {
      log.error("Ошибка удаления Passkey: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().body(createErrorResponse("Ошибка удаления Passkey"));
    }
  }

  /** Очищает истекшие challenges (для администратора) */
  @PostMapping("/cleanup")
  public ResponseEntity<Map<String, Object>> cleanupExpiredChallenges() {
    try {
      webAuthnService.cleanupExpiredChallenges();
      return ResponseEntity.ok(createSuccessResponse("Истекшие challenges очищены"));

    } catch (Exception e) {
      log.error("Ошибка очистки challenges: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().body(createErrorResponse("Ошибка очистки challenges"));
    }
  }

  /** Извлекает ID пользователя из аутентификации */
  private String getUserIdFromAuthentication(Authentication authentication) {
    if (authentication == null || authentication.getPrincipal() == null) {
      throw new RuntimeException("Пользователь не аутентифицирован");
    }

    // Простая реализация для демонстрации
    // В реальном приложении это зависит от вашей реализации UserDetails
    if (authentication.getPrincipal()
        instanceof org.springframework.security.core.userdetails.UserDetails) {
      org.springframework.security.core.userdetails.UserDetails userDetails =
          (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
      return userDetails.getUsername();
    }

    return authentication.getName();
  }

  /** Создает ответ об успехе */
  private Map<String, Object> createSuccessResponse(String message) {
    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("message", message);
    return response;
  }

  /** Создает ответ об ошибке */
  private Map<String, Object> createErrorResponse(String message) {
    Map<String, Object> response = new HashMap<>();
    response.put("success", false);
    response.put("message", message);
    return response;
  }
}
