package com.taskboard.userservice.infrastructure.persistence.entity;

import com.taskboard.userservice.domain.model.UserAudit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * JPA entity for user audit logs.
 *
 * <p>This entity represents audit records for user-related activities such as
 * login attempts, profile updates, password changes, and other security events.
 * It provides a comprehensive audit trail for compliance and security monitoring.
 *
 * <p>The UserAuditEntity includes:
 *
 * <ul>
 *   <li>User identification and action details
 *   <li>IP address and user agent for security tracking
 *   <li>Action result and error details
 *   <li>Automatic timestamping for audit trail
 * </ul>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "user_audit_logs", indexes = {
    @Index(name = "idx_user_audit_user_id", columnList = "user_id"),
    @Index(name = "idx_user_audit_action", columnList = "action"),
    @Index(name = "idx_user_audit_created_at", columnList = "created_at"),
    @Index(name = "idx_user_audit_ip_address", columnList = "ip_address")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "username", length = 50)
  private String username;

  @Column(name = "action", nullable = false, length = 50)
  private String action;

  @Column(name = "action_type", nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private UserAudit.AuditActionType actionType;

  @Column(name = "resource", length = 100)
  private String resource;

  @Column(name = "ip_address", length = 45)
  private String ipAddress;

  @Column(name = "user_agent", length = 500)
  private String userAgent;

  @Column(name = "success", nullable = false)
  private boolean success;

  @Column(name = "error_message", length = 500)
  private String errorMessage;

  @Column(name = "details", columnDefinition = "TEXT")
  private String details;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  // AuditActionType enum is defined in the domain model UserAudit

  // Getters and setters are provided by Lombok @Data

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserAuditEntity that = (UserAuditEntity) o;
    return id != null && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "UserAuditEntity{" +
        "id=" + id +
        ", userId=" + userId +
        ", username='" + username + '\'' +
        ", action='" + action + '\'' +
        ", actionType=" + actionType +
        ", success=" + success +
        ", createdAt=" + createdAt +
        '}';
  }
}
