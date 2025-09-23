package com.taskboard.api.service;

import com.taskboard.api.model.OneTimeToken;
import com.taskboard.api.repository.OneTimeTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing one-time tokens
 * Provides secure temporary access for various authentication purposes
 */
@Service
@Slf4j
@Transactional
public class OneTimeTokenService {

    @Autowired
    private OneTimeTokenRepository tokenRepository;

    @Value("${one-time-token.expiration.minutes:15}")
    private int defaultExpirationMinutes;

    @Value("${one-time-token.max-active-per-user:5}")
    private int maxActiveTokensPerUser;

    @Value("${one-time-token.length:32}")
    private int tokenLength;

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Создает одноразовый токен для входа
     */
    public OneTimeToken createLoginToken(final String userId, final int expirationMinutes) {
        return createToken(userId, OneTimeToken.TokenPurpose.LOGIN, expirationMinutes, null);
    }

    /**
     * Создает одноразовый токен для сброса пароля
     */
    public OneTimeToken createPasswordResetToken(final String userId, final int expirationMinutes) {
        return createToken(userId, OneTimeToken.TokenPurpose.PASSWORD_RESET, expirationMinutes, null);
    }

    /**
     * Создает одноразовый токен для подтверждения email
     */
    public OneTimeToken createEmailVerificationToken(final String userId, final int expirationMinutes) {
        return createToken(userId, OneTimeToken.TokenPurpose.EMAIL_VERIFICATION, expirationMinutes, null);
    }

    /**
     * Создает одноразовый токен для административного доступа
     */
    public OneTimeToken createAdminAccessToken(final String userId, final int expirationMinutes, final String metadata) {
        return createToken(userId, OneTimeToken.TokenPurpose.ADMIN_ACCESS, expirationMinutes, metadata);
    }

    /**
     * Создает одноразовый токен для API доступа
     */
    public OneTimeToken createApiAccessToken(final String userId, final int expirationMinutes, final String metadata) {
        return createToken(userId, OneTimeToken.TokenPurpose.API_ACCESS, expirationMinutes, metadata);
    }

    /**
     * Создает одноразовый токен для экстренного доступа
     */
    public OneTimeToken createEmergencyAccessToken(final String userId, final int expirationMinutes, final String metadata) {
        return createToken(userId, OneTimeToken.TokenPurpose.EMERGENCY_ACCESS, expirationMinutes, metadata);
    }

    /**
     * Создает одноразовый токен с указанными параметрами
     */
    public OneTimeToken createToken(final String userId, final OneTimeToken.TokenPurpose purpose, final int expirationMinutes, final String metadata) {
        try {
            // Проверяем лимит активных токенов
            long activeTokensCount = tokenRepository.countActiveByUserIdAndPurpose(userId, purpose, LocalDateTime.now());
            if (activeTokensCount >= maxActiveTokensPerUser) {
                log.warn("Превышен лимит активных токенов для пользователя {} и цели {}", userId, purpose);
                throw new RuntimeException("Превышен лимит активных токенов");
            }

            // Генерируем уникальный токен
            String tokenValue = generateUniqueToken();

            // Создаем токен
            OneTimeToken token = OneTimeToken.builder()
                    .token(tokenValue)
                    .userId(userId)
                    .purpose(purpose)
                    .expiresAt(LocalDateTime.now().plusMinutes(expirationMinutes))
                    .metadata(metadata)
                    .build();

            OneTimeToken savedToken = tokenRepository.save(token);

            log.info("Создан одноразовый токен для пользователя {} с целью {} (истекает через {} минут)", 
                userId, purpose, expirationMinutes);

            return savedToken;

        } catch (Exception e) {
            log.error("Ошибка создания одноразового токена: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось создать одноразовый токен", e);
        }
    }

    /**
     * Валидирует и использует одноразовый токен
     */
    public boolean validateAndUseToken(final String tokenValue, final OneTimeToken.TokenPurpose expectedPurpose) {
        try {
            // Находим активный токен
            Optional<OneTimeToken> tokenOpt = tokenRepository.findActiveByToken(tokenValue, LocalDateTime.now());
            
            if (tokenOpt.isEmpty()) {
                log.warn("Токен не найден или истек: {}", tokenValue);
                return false;
            }

            OneTimeToken token = tokenOpt.get();

            // Проверяем цель токена
            if (!token.isValidForPurpose(expectedPurpose)) {
                log.warn("Токен не подходит для цели {}: {}", expectedPurpose, tokenValue);
                return false;
            }

            // Помечаем токен как использованный
            token.markAsUsed();
            tokenRepository.markAsUsed(tokenValue, LocalDateTime.now());

            log.info("Одноразовый токен успешно использован: {} для пользователя {}", 
                tokenValue, token.getUserId());

            return true;

        } catch (Exception e) {
            log.error("Ошибка валидации токена: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Получает информацию о токене без его использования
     */
    public Optional<OneTimeToken> getTokenInfo(final String tokenValue) {
        return tokenRepository.findActiveByToken(tokenValue, LocalDateTime.now());
    }

    /**
     * Получает все активные токены пользователя
     */
    public List<OneTimeToken> getActiveTokensForUser(final String userId) {
        return tokenRepository.findByUserId(userId).stream()
                .filter(token -> !token.getIsUsed() && !token.isExpired())
                .toList();
    }

    /**
     * Получает активные токены пользователя по цели
     */
    public List<OneTimeToken> getActiveTokensForUserAndPurpose(final String userId, final OneTimeToken.TokenPurpose purpose) {
        return tokenRepository.findActiveByUserIdAndPurpose(userId, purpose, LocalDateTime.now());
    }

    /**
     * Отзывает все активные токены пользователя
     */
    public void revokeAllTokensForUser(final String userId) {
        List<OneTimeToken> activeTokens = getActiveTokensForUser(userId);
        for (OneTimeToken token : activeTokens) {
            token.markAsUsed();
            tokenRepository.markAsUsed(token.getToken(), LocalDateTime.now());
        }
        
        log.info("Отозваны все активные токены для пользователя: {}", userId);
    }

    /**
     * Отзывает токены пользователя по цели
     */
    public void revokeTokensForUserAndPurpose(final String userId, final OneTimeToken.TokenPurpose purpose) {
        List<OneTimeToken> tokens = getActiveTokensForUserAndPurpose(userId, purpose);
        for (OneTimeToken token : tokens) {
            token.markAsUsed();
            tokenRepository.markAsUsed(token.getToken(), LocalDateTime.now());
        }
        
        log.info("Отозваны токены цели {} для пользователя: {}", purpose, userId);
    }

    /**
     * Очищает истекшие токены
     */
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.debug("Очищены истекшие одноразовые токены");
    }

    /**
     * Очищает использованные токены старше указанного количества дней
     */
    @Transactional
    public void cleanupUsedTokens(final int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        tokenRepository.deleteUsedTokensOlderThan(cutoffDate);
        log.debug("Очищены использованные токены старше {} дней", daysOld);
    }

    /**
     * Генерирует уникальный токен
     */
    private String generateUniqueToken() {
        String token;
        int attempts = 0;
        final int maxAttempts = 10;

        do {
            token = generateRandomToken();
            attempts++;
            
            if (attempts >= maxAttempts) {
                throw new RuntimeException("Не удалось сгенерировать уникальный токен после " + maxAttempts + " попыток");
            }
        } while (tokenRepository.findByToken(token).isPresent());

        return token;
    }

    /**
     * Генерирует случайный токен
     */
    private String generateRandomToken() {
        byte[] bytes = new byte[tokenLength];
        secureRandom.nextBytes(bytes);
        
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        
        return sb.toString();
    }

    /**
     * Проверяет, действителен ли токен для указанной цели
     */
    public boolean isTokenValid(final String tokenValue, final OneTimeToken.TokenPurpose purpose) {
        Optional<OneTimeToken> tokenOpt = getTokenInfo(tokenValue);
        return tokenOpt.isPresent() && tokenOpt.get().isValidForPurpose(purpose);
    }

    /**
     * Получает время истечения токена
     */
    public Optional<LocalDateTime> getTokenExpiration(final String tokenValue) {
        return getTokenInfo(tokenValue).map(OneTimeToken::getExpiresAt);
    }
}
