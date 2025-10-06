package com.taskboard.api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import static lombok.AccessLevel.PROTECTED;
import org.hibernate.annotations.CreationTimestamp;

/** Модель для хранения WebAuthn challenges */
@Entity
@Table(name = "webauthn_challenges")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class WebAuthnChallenge {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @EqualsAndHashCode.Include
  private UUID id;

  @Column(name = "challenge", nullable = false, unique = true, length = 500)
  private String challenge;

  @Column(name = "user_id")
  private String userId;

  @Column(name = "type", nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private ChallengeType type;

  @Column(name = "is_used", nullable = false)
  @Builder.Default
  private Boolean isUsed = false;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  public enum ChallengeType {
    REGISTRATION,
    AUTHENTICATION
  }

  /** Проверяет, истек ли challenge */
  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiresAt);
  }

  /** Проверяет, можно ли использовать challenge */
  public boolean canBeUsed() {
    return !isUsed && !isExpired();
  }
}
