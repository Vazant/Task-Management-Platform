package com.taskboard.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Model for one-time tokens used for various authentication purposes
 * Provides secure temporary access for login, password reset, email verification, etc.
 */
@Entity
@Table(name = "one_time_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OneTimeToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "token", nullable = false, unique = true, length = 100)
    private String token;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "purpose", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private TokenPurpose purpose;

    @Column(name = "is_used", nullable = false)
    @Builder.Default
    private Boolean isUsed = false;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum TokenPurpose {
        LOGIN,              // Temporary login access
        PASSWORD_RESET,     // Password reset
        EMAIL_VERIFICATION, // Email verification
        ADMIN_ACCESS,       // Temporary admin access
        API_ACCESS,         // Temporary API access
        EMERGENCY_ACCESS    // Emergency access
    }

    /**
     * Checks if the token has expired
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Checks if the token can be used (not used and not expired)
     */
    public boolean canBeUsed() {
        return !isUsed && !isExpired();
    }

    /**
     * Marks the token as used
     */
    public void markAsUsed() {
        this.isUsed = true;
        this.usedAt = LocalDateTime.now();
    }

    /**
     * Checks if the token is valid for the specified purpose
     */
    public boolean isValidForPurpose(TokenPurpose purpose) {
        return this.purpose == purpose && canBeUsed();
    }
}
