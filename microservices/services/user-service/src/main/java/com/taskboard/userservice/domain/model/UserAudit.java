package com.taskboard.userservice.domain.model;

import java.time.LocalDateTime;

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
public class UserAudit {

  private Long id;
  private Long userId;
  private String username;
  private String action;
  private AuditActionType actionType;
  private String resource;
  private String ipAddress;
  private String userAgent;
  private boolean success;
  private String errorMessage;
  private String details;
  private LocalDateTime createdAt;

  /**
   * Protected constructor for JPA.
   */
  protected UserAudit() {
    // Required by JPA
  }

  /**
   * Creates a new UserAudit instance.
   *
   * @param userId the user ID
   * @param username the username
   * @param action the action performed
   * @param actionType the type of action
   * @param ipAddress the IP address
   * @param success whether the action was successful
   */
  public UserAudit(Long userId, String username, String action, AuditActionType actionType, 
                   String ipAddress, boolean success) {
    this.userId = userId;
    this.username = username;
    this.action = action;
    this.actionType = actionType;
    this.ipAddress = ipAddress;
    this.success = success;
    this.createdAt = LocalDateTime.now();
  }

  /**
   * Creates a new UserAudit instance with full details.
   *
   * @param userId the user ID
   * @param username the username
   * @param action the action performed
   * @param actionType the type of action
   * @param resource the resource accessed
   * @param ipAddress the IP address
   * @param userAgent the user agent
   * @param success whether the action was successful
   * @param errorMessage the error message if failed
   * @param details additional details
   */
  public UserAudit(Long userId, String username, String action, AuditActionType actionType,
                   String resource, String ipAddress, String userAgent, boolean success,
                   String errorMessage, String details) {
    this.userId = userId;
    this.username = username;
    this.action = action;
    this.actionType = actionType;
    this.resource = resource;
    this.ipAddress = ipAddress;
    this.userAgent = userAgent;
    this.success = success;
    this.errorMessage = errorMessage;
    this.details = details;
    this.createdAt = LocalDateTime.now();
  }

  /**
   * Validates the audit record.
   *
   * @throws IllegalArgumentException if the audit record is invalid
   */
  public void validate() {
    if (action == null || action.trim().isEmpty()) {
      throw new IllegalArgumentException("Action cannot be null or empty");
    }
    if (actionType == null) {
      throw new IllegalArgumentException("Action type cannot be null");
    }
    if (ipAddress != null && ipAddress.length() > 45) {
      throw new IllegalArgumentException("IP address cannot exceed 45 characters");
    }
    if (username != null && username.length() > 50) {
      throw new IllegalArgumentException("Username cannot exceed 50 characters");
    }
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

  // Getters
  public Long getId() {
    return id;
  }

  public Long getUserId() {
    return userId;
  }

  public String getUsername() {
    return username;
  }

  public String getAction() {
    return action;
  }

  public AuditActionType getActionType() {
    return actionType;
  }

  public String getResource() {
    return resource;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public String getDetails() {
    return details;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  // Protected setters for JPA
  protected void setId(Long id) {
    this.id = id;
  }

  protected void setUserId(Long userId) {
    this.userId = userId;
  }

  protected void setUsername(String username) {
    this.username = username;
  }

  protected void setAction(String action) {
    this.action = action;
  }

  protected void setActionType(AuditActionType actionType) {
    this.actionType = actionType;
  }

  protected void setResource(String resource) {
    this.resource = resource;
  }

  protected void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  protected void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  protected void setSuccess(boolean success) {
    this.success = success;
  }

  protected void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  protected void setDetails(String details) {
    this.details = details;
  }

  protected void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserAudit userAudit = (UserAudit) o;
    return id != null && id.equals(userAudit.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "UserAudit{" +
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
