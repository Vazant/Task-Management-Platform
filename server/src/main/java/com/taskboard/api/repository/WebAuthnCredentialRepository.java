package com.taskboard.api.repository;

import com.taskboard.user.model.WebAuthnCredential;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Репозиторий для работы с WebAuthn credentials */
@Repository
public interface WebAuthnCredentialRepository extends JpaRepository<WebAuthnCredential, UUID> {

  /** Находит credential по credential ID */
  Optional<WebAuthnCredential> findByCredentialId(String credentialId);

  /** Находит все активные credentials для пользователя */
  @Query("SELECT w FROM WebAuthnCredential w WHERE w.userId = :userId")
  List<WebAuthnCredential> findActiveByUserId(@Param("userId") String userId);

  /** Находит все credentials для пользователя */
  List<WebAuthnCredential> findByUserId(String userId);

  /** Проверяет существование credential по credential ID */
  boolean existsByCredentialId(String credentialId);

  /** Находит credential по credential ID и пользователю */
  Optional<WebAuthnCredential> findByCredentialIdAndUserId(String credentialId, String userId);

  /** Обновляет счетчик для credential */
  @Query(
      "UPDATE WebAuthnCredential w SET w.counter = :counter, w.lastUsedAt = CURRENT_TIMESTAMP WHERE w.credentialId = :credentialId")
  void updateCounterAndLastUsed(
      @Param("credentialId") String credentialId, @Param("counter") Long counter);

  /** Деактивирует credential */
  @Query("UPDATE WebAuthnCredential w SET w.isActive = false WHERE w.credentialId = :credentialId")
  void deactivateCredential(@Param("credentialId") String credentialId);
}
