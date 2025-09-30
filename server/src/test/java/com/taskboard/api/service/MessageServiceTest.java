package com.taskboard.api.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для MessageService и i18n функциональности.
 */
@SpringBootTest
@ActiveProfiles("test")
public class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageSource messageSource;

    @Test
    public void testGetMessageWithDefaultLocale() {
        // Тест получения сообщения с локалью по умолчанию (EN)
        // Явно устанавливаем английскую локаль для теста
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        String message = messageService.getMessage("auth.login.success");
        assertEquals("Login successful", message);
    }

    @Test
    public void testGetMessageWithEnglishLocale() {
        // Тест получения сообщения на английском языке
        String message = messageService.getMessage("auth.login.success", Locale.ENGLISH);
        assertEquals("Login successful", message);
    }

    @Test
    public void testGetMessageWithRussianLocale() {
        // Тест получения сообщения на русском языке
        String message = messageService.getMessage("auth.login.success", Locale.forLanguageTag("ru"));
        assertEquals("Вход выполнен успешно", message);
    }

    @Test
    public void testGetMessageWithParameters() {
        // Тест получения сообщения с параметрами
        String message = messageService.getMessage("info.test.auth", new Object[]{"john.doe"}, Locale.ENGLISH);
        assertEquals("Authenticated as: john.doe", message);

        String messageRu = messageService.getMessage("info.test.auth", new Object[]{"john.doe"}, Locale.forLanguageTag("ru"));
        assertEquals("Аутентифицирован как: john.doe", messageRu);
    }

    @Test
    public void testGetMessageWithDefaultMessage() {
        // Тест получения сообщения с дефолтным значением
        String message = messageService.getMessage("nonexistent.key", "Default message", Locale.ENGLISH);
        assertEquals("Default message", message);
    }

    @Test
    public void testValidationMessages() {
        // Тест сообщений валидации
        String message = messageService.getMessage("validation.email.required", Locale.ENGLISH);
        assertEquals("Email is required", message);

        String messageRu = messageService.getMessage("validation.email.required", Locale.forLanguageTag("ru"));
        assertEquals("Email обязателен", messageRu);
    }

    @Test
    public void testErrorMessages() {
        // Тест сообщений об ошибках
        String message = messageService.getMessage("error.global.unexpected", new Object[]{"Test error"}, Locale.ENGLISH);
        assertEquals("Internal server error: Test error", message);

        String messageRu = messageService.getMessage("error.global.unexpected", new Object[]{"Test error"}, Locale.forLanguageTag("ru"));
        assertEquals("Внутренняя ошибка сервера: Test error", messageRu);
    }

    @Test
    public void testMessageSourceDirectly() {
        // Тест прямого использования MessageSource
        String message = messageSource.getMessage("auth.register.success", null, Locale.ENGLISH);
        assertEquals("Registration successful", message);

        String messageRu = messageSource.getMessage("auth.register.success", null, Locale.forLanguageTag("ru"));
        assertEquals("Регистрация выполнена успешно", messageRu);
    }

    @Test
    public void testAllMessageKeysExist() {
        // Тест проверки существования всех ключей сообщений
        String[] testKeys = {
            "auth.login.success",
            "auth.login.failure.credentials",
            "auth.register.success",
            "auth.register.failure.passwords_mismatch",
            "auth.register.failure.email_exists",
            "auth.register.failure.username_exists",
            "auth.register.failure.generic",
            "auth.refresh.required",
            "auth.refresh.success",
            "auth.refresh.failure.invalid",
            "auth.password.reset.sent",
            "auth.password.reset.success",
            "auth.password.reset.token_required",
            "auth.password.reset.password_required",
            "auth.password.reset.instruction",
            "validation.request.invalid",
            "validation.email.required",
            "validation.email.invalid",
            "validation.username.required",
            "validation.username.size",
            "validation.password.required",
            "validation.password.size",
            "validation.password.confirm.required",
            "validation.password.confirm.mismatch",
            "validation.password.pattern",
            "validation.file.name.required",
            "validation.file.name.size",
            "validation.file.content_type.required",
            "validation.file.content_type.invalid",
            "validation.file.size.max",
            "validation.storage.key.required",
            "validation.user.id.required",
            "validation.version.positive",
            "validation.file.size.positive",
            "error.global.unexpected",
            "error.data.integrity",
            "error.data.integrity.email_exists",
            "error.data.integrity.username_exists",
            "error.dpop.invalid_proof",
            "error.dpop.expired_proof",
            "error.dpop.method_mismatch",
            "error.dpop.url_mismatch",
            "error.dpop.nonce_mismatch",
            "error.dpop.hash_mismatch",
            "error.webauthn.user_not_authenticated",
            "error.webauthn.challenge_expired",
            "error.webauthn.no_credentials",
            "error.webauthn.unauthorized_delete",
            "error.storage.endpoint_required",
            "error.storage.access_key_required",
            "error.storage.secret_key_required",
            "error.storage.bucket_required",
            "error.storage.upload_failed",
            "error.storage.download_failed",
            "error.storage.file_upload_failed",
            "error.storage.file_delete_failed",
            "error.storage.metadata_failed",
            "error.storage.file_not_found",
            "error.storage.file_size_exceeded",
            "error.ott.limit_exceeded",
            "error.ott.creation_failed",
            "error.ott.generation_failed",
            "error.profile.username_exists",
            "error.profile.email_exists",
            "error.profile.invalid_current_password",
            "success.auth.login",
            "success.auth.register",
            "success.auth.refresh",
            "success.auth.password_reset",
            "success.profile.updated",
            "success.avatar.uploaded",
            "success.avatar.confirmed",
            "success.avatar.deleted",
            "info.test.auth",
            "info.test.auth_failed",
            "info.test.public",
            "info.security.csrf_disabled",
            "info.security.dpop_enabled",
            "info.security.jwt_enabled",
            "info.security.oauth2_resource",
            "info.security.oauth2_client",
            "info.security.webauthn_placeholder",
            "info.security.ott_configured",
            "info.security.configured",
            "config.security.permit_all_paths",
            "config.security.dpop_enabled",
            "config.security.oauth2_enabled",
            "config.security.webauthn_enabled",
            "config.security.ott_enabled",
            "cart.items.count",
            "user.tasks.count",
            "user.projects.count"
        };

        for (String key : testKeys) {
            // Проверяем, что ключ существует для английского языка
            assertDoesNotThrow(() -> {
                String message = messageSource.getMessage(key, null, Locale.ENGLISH);
                assertNotNull(message, "Message for key " + key + " should not be null");
                assertFalse(message.trim().isEmpty(), "Message for key " + key + " should not be empty");
            }, "Key " + key + " should exist in English messages");

            // Проверяем, что ключ существует для русского языка
            assertDoesNotThrow(() -> {
                String message = messageSource.getMessage(key, null, Locale.forLanguageTag("ru"));
                assertNotNull(message, "Message for key " + key + " should not be null");
                assertFalse(message.trim().isEmpty(), "Message for key " + key + " should not be empty");
            }, "Key " + key + " should exist in Russian messages");
        }
    }

    @Test
    public void testICUPluralization() {
        // Тест ICU MessageFormat для множественного числа
        String message1 = messageService.getMessage("cart.items.count", 1, Locale.ENGLISH);
        assertEquals("1 item", message1);

        String message2 = messageService.getMessage("cart.items.count", 2, Locale.ENGLISH);
        assertEquals("2 items", message2);

        String messageRu1 = messageService.getMessage("cart.items.count", 1, Locale.forLanguageTag("ru"));
        assertEquals("1 товар", messageRu1);

        String messageRu2 = messageService.getMessage("cart.items.count", 2, Locale.forLanguageTag("ru"));
        assertEquals("2 товара", messageRu2);

        String messageRu5 = messageService.getMessage("cart.items.count", 5, Locale.forLanguageTag("ru"));
        assertEquals("5 товаров", messageRu5);
    }
}
