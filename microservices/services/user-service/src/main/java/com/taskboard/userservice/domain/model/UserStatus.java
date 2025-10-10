package com.taskboard.userservice.domain.model;

/**
 * Enumeration representing the status of a user account.
 *
 * <p>User status determines the current state of a user account and affects their ability to
 * perform actions within the system. This enum encapsulates all possible user states and provides
 * business logic for status transitions.
 *
 * <p>Possible statuses:
 *
 * <ul>
 *   <li>ACTIVE - User can log in and use the system
 *   <li>INACTIVE - User account is temporarily disabled
 *   <li>BLOCKED - User account is blocked due to policy violations
 *   <li>PENDING - User account is awaiting activation
 * </ul>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
public enum UserStatus {

  /** User account is active and can be used normally. */
  ACTIVE("Active", "User account is active and can be used normally"),

  /** User account is inactive and temporarily disabled. */
  INACTIVE("Inactive", "User account is temporarily disabled"),

  /** User account is blocked due to policy violations or security reasons. */
  BLOCKED("Blocked", "User account is blocked due to policy violations"),

  /** User account is pending activation or verification. */
  PENDING("Pending", "User account is awaiting activation or verification");

  private final String displayName;
  private final String description;

  /**
   * Constructor for UserStatus enum.
   *
   * @param displayName human-readable display name
   * @param description detailed description of the status
   */
  UserStatus(String displayName, String description) {
    this.displayName = displayName;
    this.description = description;
  }

  /**
   * Gets the human-readable display name of the status.
   *
   * @return display name
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Gets the detailed description of the status.
   *
   * @return status description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Checks if the status allows user to log in.
   *
   * @return true if user can log in with this status
   */
  public boolean canLogin() {
    return this == ACTIVE;
  }

  /**
   * Checks if the status allows user to perform actions.
   *
   * @return true if user can perform actions with this status
   */
  public boolean canPerformActions() {
    return this == ACTIVE;
  }

  /**
   * Checks if the status indicates a problematic state.
   *
   * @return true if status indicates a problem
   */
  public boolean isProblematic() {
    return this == BLOCKED;
  }

  /**
   * Gets the status from its display name.
   *
   * @param displayName the display name to search for
   * @return the matching UserStatus or null if not found
   */
  public static UserStatus fromDisplayName(String displayName) {
    for (UserStatus status : values()) {
      if (status.displayName.equalsIgnoreCase(displayName)) {
        return status;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return displayName;
  }
}
