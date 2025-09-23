package com.taskboard.api.repository;

import com.taskboard.api.model.WebAuthnCredential;
import com.taskboard.api.model.WebAuthnChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JDBC repository for WebAuthn credentials and challenges
 * Implements Spring Security 6.5 WebAuthn repository interfaces
 */
@Repository
public interface JdbcWebAuthnRepository extends JpaRepository<WebAuthnCredential, UUID> {

    /**
     * Find WebAuthn credential by credential ID
     */
    @Query("SELECT w FROM WebAuthnCredential w WHERE w.credentialId = :credentialId AND w.isActive = true")
    Optional<WebAuthnCredential> findActiveByCredentialId(@Param("credentialId") String credentialId);

    /**
     * Find all active credentials for a user
     */
    @Query("SELECT w FROM WebAuthnCredential w WHERE w.userId = :userId AND w.isActive = true")
    List<WebAuthnCredential> findActiveByUserId(@Param("userId") String userId);

    /**
     * Find WebAuthn challenge by challenge value
     */
    @Query("SELECT c FROM WebAuthnChallenge c WHERE c.challenge = :challenge AND c.isUsed = false AND c.expiresAt > :now")
    Optional<WebAuthnChallenge> findActiveChallenge(@Param("challenge") String challenge, @Param("now") LocalDateTime now);

    /**
     * Find active challenges for a user
     */
    @Query("SELECT c FROM WebAuthnChallenge c WHERE c.userId = :userId AND c.isUsed = false AND c.expiresAt > :now")
    List<WebAuthnChallenge> findActiveChallengesByUserId(@Param("userId") String userId, @Param("now") LocalDateTime now);

    /**
     * Mark challenge as used
     */
    @Query("UPDATE WebAuthnChallenge c SET c.isUsed = true WHERE c.challenge = :challenge")
    void markChallengeAsUsed(@Param("challenge") String challenge);

    /**
     * Clean up expired challenges
     */
    @Query("DELETE FROM WebAuthnChallenge c WHERE c.expiresAt < :now")
    void deleteExpiredChallenges(@Param("now") LocalDateTime now);

    /**
     * Update credential counter
     */
    @Query("UPDATE WebAuthnCredential w SET w.counter = :counter, w.lastUsedAt = :lastUsedAt WHERE w.credentialId = :credentialId")
    void updateCredentialCounter(@Param("credentialId") String credentialId, @Param("counter") Long counter, @Param("lastUsedAt") LocalDateTime lastUsedAt);
}
