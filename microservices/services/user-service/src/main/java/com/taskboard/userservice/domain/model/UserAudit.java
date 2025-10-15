package com.taskboard.userservice.domain.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Domain model for user audit logs.
 *
 * <p>This class represents audit records for user-related activities such as
 * login attempts, profile updates, password changes, and other security events.
 * It encapsulates the business logic and validation rules for audit data.
 *
 * <p>The UserAudit model includes:
 *
 * <ul>
 *   <li>User identification and action details
 *   <li>IP address and user agent for security tracking
 *   <li>Action result and error details
 *   <li>Business validation rules
 * </ul>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"userId", "action", "actionType", "createdAt"}) // Use significant fields
@ToString(exclude = {"details", "errorMessage"}) // Exclude large fields
public class UserAudit {

  private Long id;
  
  @NotNull
  private Long userId;
  
  @Size(max = 50)
  private String username;
  
  @NotBlank
  private String action;
  
  @NotNull
  private AuditActionType actionType;
  
  private String resource;
  
  @Size(max = 45)
  private String ipAddress;
  
  private String userAgent;
  
  private boolean success;
  
  private String errorMessage;
  
  private String details;
  
  private LocalDateTime createdAt;

  // Lombok @NoArgsConstructor and @AllArgsConstructor provide constructors automatically

  /**
   * Sets the creation timestamp to current time.
   * Should be called when creating new audit record.
   */
  public void setCreatedNow() {
    this.createdAt = LocalDateTime.now();
  }

  /**
   * Checks if this is a security-related action.
   *
   * @return true if this is a security-related action
   */
  public boolean isSecurityAction() {
    return actionType == AuditActionType.LOGIN_ATTEMPT ||
           actionType == AuditActionType.LOGIN_SUCCESS ||
           actionType == AuditActionType.LOGIN_FAILURE ||
           actionType == AuditActionType.ACCOUNT_LOCKED ||
           actionType == AuditActionType.AUTHORIZATION_FAILURE ||
           actionType == AuditActionType.SUSPICIOUS_ACTIVITY;
  }

  /**
   * Checks if this is a failed action.
   *
   * @return true if this is a failed action
   */
  public boolean isFailedAction() {
    return !success;
  }

  /**
   * Gets a summary of the audit record.
   *
   * @return a summary string
   */
  public String getSummary() {
    return String.format("%s %s for user %s from IP %s - %s",
        actionType, action, username, ipAddress, success ? "SUCCESS" : "FAILED");
  }

  // Lombok @Data annotation provides all getters and setters automatically

  /**
   * Enumeration of audit action types.
   */
  public enum AuditActionType {
    LOGIN_ATTEMPT,
    LOGIN_SUCCESS,
    LOGIN_FAILURE,
    LOGOUT,
    PASSWORD_CHANGE,
    PROFILE_UPDATE,
    ACCOUNT_LOCKED,
    ACCOUNT_UNLOCKED,
    EMAIL_VERIFICATION,
    AUTHORIZATION_FAILURE,
    SUSPICIOUS_ACTIVITY,
    DATA_ACCESS,
    DATA_MODIFICATION,
    ACCOUNT_DELETION
  }

  // Lombok @Data annotation provides equals, hashCode, and toString automatically
}
