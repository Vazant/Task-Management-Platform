package com.taskboard.api.repository;

import com.taskboard.api.model.OneTimeToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с одноразовыми токенами
 */
@Repository
public interface OneTimeTokenRepository extends JpaRepository<OneTimeToken, UUID> {

    /**
     * Находит токен по значению
     */
    Optional<OneTimeToken> findByToken(String token);

    /**
     * Находит активный токен по значению
     */
    @Query("SELECT t FROM OneTimeToken t WHERE t.token = :token AND t.isUsed = false AND t.expiresAt > :now")
    Optional<OneTimeToken> findActiveByToken(@Param("token") String token, @Param("now") LocalDateTime now);

    /**
     * Находит активные токены пользователя по цели
     */
    @Query("SELECT t FROM OneTimeToken t WHERE t.userId = :userId AND t.purpose = :purpose AND t.isUsed = false AND t.expiresAt > :now")
    List<OneTimeToken> findActiveByUserIdAndPurpose(@Param("userId") String userId, @Param("purpose") OneTimeToken.TokenPurpose purpose, @Param("now") LocalDateTime now);

    /**
     * Находит все токены пользователя
     */
    List<OneTimeToken> findByUserId(String userId);

    /**
     * Находит токены по цели
     */
    List<OneTimeToken> findByPurpose(OneTimeToken.TokenPurpose purpose);

    /**
     * Помечает токен как использованный
     */
    @Modifying
    @Query("UPDATE OneTimeToken t SET t.isUsed = true, t.usedAt = :usedAt WHERE t.token = :token")
    void markAsUsed(@Param("token") String token, @Param("usedAt") LocalDateTime usedAt);

    /**
     * Удаляет истекшие токены
     */
    @Modifying
    @Query("DELETE FROM OneTimeToken t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * Удаляет использованные токены старше указанной даты
     */
    @Modifying
    @Query("DELETE FROM OneTimeToken t WHERE t.isUsed = true AND t.usedAt < :cutoffDate")
    void deleteUsedTokensOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Подсчитывает количество активных токенов пользователя по цели
     */
    @Query("SELECT COUNT(t) FROM OneTimeToken t WHERE t.userId = :userId AND t.purpose = :purpose AND t.isUsed = false AND t.expiresAt > :now")
    long countActiveByUserIdAndPurpose(@Param("userId") String userId, @Param("purpose") OneTimeToken.TokenPurpose purpose, @Param("now") LocalDateTime now);

    /**
     * Находит токены, созданные в указанном диапазоне времени
     */
    @Query("SELECT t FROM OneTimeToken t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    List<OneTimeToken> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
