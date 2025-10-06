package com.taskboard.api.controller;

import com.taskboard.api.model.OneTimeToken;
import com.taskboard.api.service.OneTimeTokenService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** Контроллер для работы с одноразовыми токенами входа */
@RestController
@RequestMapping("/api/one-time-tokens")
@Slf4j
public class OneTimeTokenController {

  @Autowired private OneTimeTokenService tokenService;

  /** Создает одноразовый токен для входа */
  @PostMapping("/login")
  public ResponseEntity<Map<String, Object>> createLoginToken(
      @RequestParam String userId,
      @RequestParam(defaultValue = "15") int expirationMinutes,
      Authentication authentication) {

    try {
      // Проверяем права доступа (только админ или сам пользователь)
      if (!hasPermissionToCreateToken(userId, authentication)) {
        return ResponseEntity.status(403)
            .body(createErrorResponse("Недостаточно прав для создания токена"));
      }

      OneTimeToken token = tokenService.createLoginToken(userId, expirationMinutes);

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("token", token.getToken());
      response.put("expiresAt", token.getExpiresAt());
      response.put("purpose", token.getPurpose());

      log.info("Создан токен для входа пользователя: {}", userId);
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Ошибка создания токена для входа: {}", e.getMessage(), e);
      return ResponseEntity.badRequest()
          .body(createErrorResponse("Не удалось создать токен для входа"));
    }
  }

  /** Создает одноразовый токен для сброса пароля */
  @PostMapping("/password-reset")
  public ResponseEntity<Map<String, Object>> createPasswordResetToken(
      @RequestParam String userId, @RequestParam(defaultValue = "60") int expirationMinutes) {

    try {
      OneTimeToken token = tokenService.createPasswordResetToken(userId, expirationMinutes);

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("token", token.getToken());
      response.put("expiresAt", token.getExpiresAt());

      log.info("Создан токен для сброса пароля пользователя: {}", userId);
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Ошибка создания токена для сброса пароля: {}", e.getMessage(), e);
      return ResponseEntity.badRequest()
          .body(createErrorResponse("Не удалось создать токен для сброса пароля"));
    }
  }

  /** Создает одноразовый токен для административного доступа */
  @PostMapping("/admin-access")
  public ResponseEntity<Map<String, Object>> createAdminAccessToken(
      @RequestParam String userId,
      @RequestParam(defaultValue = "30") int expirationMinutes,
      @RequestParam(required = false) String metadata,
      Authentication authentication) {

    try {
      // Проверяем права администратора
      if (!isAdmin(authentication)) {
        return ResponseEntity.status(403)
            .body(createErrorResponse("Требуются права администратора"));
      }

      OneTimeToken token = tokenService.createAdminAccessToken(userId, expirationMinutes, metadata);

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("token", token.getToken());
      response.put("expiresAt", token.getExpiresAt());
      response.put("metadata", token.getMetadata());

      log.info("Создан токен административного доступа для пользователя: {}", userId);
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Ошибка создания токена административного доступа: {}", e.getMessage(), e);
      return ResponseEntity.badRequest()
          .body(createErrorResponse("Не удалось создать токен административного доступа"));
    }
  }

  /** Валидирует одноразовый токен */
  @PostMapping("/validate")
  public ResponseEntity<Map<String, Object>> validateToken(
      @RequestParam String token, @RequestParam OneTimeToken.TokenPurpose purpose) {

    try {
      boolean isValid = tokenService.validateAndUseToken(token, purpose);

      Map<String, Object> response = new HashMap<>();
      response.put("success", isValid);
      response.put("message", isValid ? "Токен действителен" : "Токен недействителен или истек");

      if (isValid) {
        log.info("Токен успешно валидирован: {}", token);
      } else {
        log.warn("Попытка использования недействительного токена: {}", token);
      }

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Ошибка валидации токена: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().body(createErrorResponse("Ошибка валидации токена"));
    }
  }

  /** Получает информацию о токене */
  @GetMapping("/info/{token}")
  public ResponseEntity<Map<String, Object>> getTokenInfo(@PathVariable String token) {
    try {
      Optional<OneTimeToken> tokenOpt = tokenService.getTokenInfo(token);

      if (tokenOpt.isEmpty()) {
        return ResponseEntity.notFound().build();
      }

      OneTimeToken tokenEntity = tokenOpt.get();
      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("purpose", tokenEntity.getPurpose());
      response.put("expiresAt", tokenEntity.getExpiresAt());
      response.put("isUsed", tokenEntity.getIsUsed());
      response.put("isExpired", tokenEntity.isExpired());
      response.put("canBeUsed", tokenEntity.canBeUsed());

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Ошибка получения информации о токене: {}", e.getMessage(), e);
      return ResponseEntity.badRequest()
          .body(createErrorResponse("Ошибка получения информации о токене"));
    }
  }

  /** Получает активные токены пользователя */
  @GetMapping("/user/{userId}")
  public ResponseEntity<List<OneTimeToken>> getUserTokens(
      @PathVariable String userId, Authentication authentication) {

    try {
      // Проверяем права доступа
      if (!hasPermissionToViewTokens(userId, authentication)) {
        return ResponseEntity.status(403).build();
      }

      List<OneTimeToken> tokens = tokenService.getActiveTokensForUser(userId);
      return ResponseEntity.ok(tokens);

    } catch (Exception e) {
      log.error("Ошибка получения токенов пользователя: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().build();
    }
  }

  /** Отзывает все токены пользователя */
  @DeleteMapping("/user/{userId}")
  public ResponseEntity<Map<String, Object>> revokeUserTokens(
      @PathVariable String userId, Authentication authentication) {

    try {
      // Проверяем права доступа
      if (!hasPermissionToRevokeTokens(userId, authentication)) {
        return ResponseEntity.status(403)
            .body(createErrorResponse("Недостаточно прав для отзыва токенов"));
      }

      tokenService.revokeAllTokensForUser(userId);

      log.info("Отозваны все токены пользователя: {}", userId);
      return ResponseEntity.ok(createSuccessResponse("Все токены пользователя отозваны"));

    } catch (Exception e) {
      log.error("Ошибка отзыва токенов пользователя: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().body(createErrorResponse("Ошибка отзыва токенов"));
    }
  }

  /** Очищает истекшие токены (для администратора) */
  @PostMapping("/cleanup")
  public ResponseEntity<Map<String, Object>> cleanupExpiredTokens(Authentication authentication) {
    try {
      // Проверяем права администратора
      if (!isAdmin(authentication)) {
        return ResponseEntity.status(403)
            .body(createErrorResponse("Требуются права администратора"));
      }

      tokenService.cleanupExpiredTokens();

      log.info("Очищены истекшие токены");
      return ResponseEntity.ok(createSuccessResponse("Истекшие токены очищены"));

    } catch (Exception e) {
      log.error("Ошибка очистки токенов: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().body(createErrorResponse("Ошибка очистки токенов"));
    }
  }

  /** Проверяет права на создание токена */
  private boolean hasPermissionToCreateToken(String userId, Authentication authentication) {
    if (authentication == null) return false;

    // Админ может создавать токены для любого пользователя
    if (isAdmin(authentication)) return true;

    // Пользователь может создавать токены только для себя
    UUID currentUserId = getCurrentUserId(authentication);
    return currentUserId != null && currentUserId.equals(userId);
  }

  /** Проверяет права на просмотр токенов */
  private boolean hasPermissionToViewTokens(String userId, Authentication authentication) {
    return hasPermissionToCreateToken(userId, authentication);
  }

  /** Проверяет права на отзыв токенов */
  private boolean hasPermissionToRevokeTokens(String userId, Authentication authentication) {
    return hasPermissionToCreateToken(userId, authentication);
  }

  /** Проверяет, является ли пользователь администратором */
  private boolean isAdmin(Authentication authentication) {
    if (authentication == null) return false;

    return authentication.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
  }

  /** Получает ID текущего пользователя */
  private UUID getCurrentUserId(Authentication authentication) {
    if (authentication == null || authentication.getPrincipal() == null) {
      return null;
    }

    // Здесь должна быть логика извлечения userId из authentication
    // В реальном приложении это зависит от вашей реализации UserDetails
    return null;
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
