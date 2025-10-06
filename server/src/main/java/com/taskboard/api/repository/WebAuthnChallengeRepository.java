package com.taskboard.api.repository;

import com.taskboard.api.model.WebAuthnChallenge;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Репозиторий для работы с WebAuthn challenges */
@Repository
public interface WebAuthnChallengeRepository extends JpaRepository<WebAuthnChallenge, UUID> {

  /** Находит challenge по значению */
  Optional<WebAuthnChallenge> findByChallenge(String challenge);

  /** Находит активный challenge для пользователя */
  @Query("SELECT w FROM WebAuthnChallenge w WHERE w.userId = :userId AND w.expiresAt > :now")
  Optional<WebAuthnChallenge> findActiveByUserId(
      @Param("userId") String userId, @Param("now") LocalDateTime now);

  /** Находит активный challenge по значению */
  @Query(
      "SELECT w FROM WebAuthnChallenge w WHERE w.challenge = :challenge AND w.isUsed = false AND w.expiresAt > :now")
  Optional<WebAuthnChallenge> findActiveByChallenge(
      @Param("challenge") String challenge, @Param("now") LocalDateTime now);

  /** Помечает challenge как использованный */
  @Modifying
  @Query("UPDATE WebAuthnChallenge w SET w.isUsed = true WHERE w.challenge = :challenge")
  void markAsUsed(@Param("challenge") String challenge);

  /** Удаляет истекшие challenges */
  @Modifying
  @Query("DELETE FROM WebAuthnChallenge w WHERE w.expiresAt < :now")
  void deleteExpiredChallenges(@Param("now") LocalDateTime now);

  /** Находит challenge по значению и типу */
  Optional<WebAuthnChallenge> findByChallengeAndType(
      String challenge, WebAuthnChallenge.ChallengeType type);
}
