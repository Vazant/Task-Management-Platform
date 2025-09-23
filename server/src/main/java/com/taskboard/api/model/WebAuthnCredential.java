package com.taskboard.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Модель для хранения WebAuthn credentials (Passkeys)
 */
@Entity
@Table(name = "webauthn_credentials")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebAuthnCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "credential_id", nullable = false, unique = true, length = 500)
    private String credentialId;

    @Column(name = "public_key", nullable = false, columnDefinition = "TEXT")
    private String publicKey;

    @Column(name = "counter", nullable = false)
    private Long counter;

    @Column(name = "aaguid", length = 36)
    private String aaguid;

    @Column(name = "credential_type", length = 50)
    private String credentialType;

    @Column(name = "attestation_type", length = 50)
    private String attestationType;

    @Column(name = "transports", length = 200)
    private String transports;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Связь с пользователем
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
