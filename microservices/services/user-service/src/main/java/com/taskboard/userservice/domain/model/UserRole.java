package com.taskboard.userservice.domain.model;

import java.util.Set;

/**
 * Enumeration representing the roles that users can have in the system.
 *
 * <p>User roles define the permissions and access levels that users have within the system. This
 * enum encapsulates all possible user roles and provides business logic for role-based access
 * control.
 *
 * <p>Role hierarchy (from highest to lowest privilege):
 *
 * <ul>
 *   <li>SUPER_ADMIN - Full system access and administration
 *   <li>ADMIN - System administration and user management
 *   <li>MANAGER - Project and team management
 *   <li>USER - Standard user with basic permissions
 *   <li>GUEST - Limited access for external users
 * </ul>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
public enum UserRole {

  /** Super administrator with full system access. */
  SUPER_ADMIN("Super Admin", "Full system access and administration", 100),

  /** Administrator with system administration capabilities. */
  ADMIN("Admin", "System administration and user management", 80),

  /** Manager with project and team management capabilities. */
  MANAGER("Manager", "Project and team management", 60),

  /** Standard user with basic system permissions. */
  USER("User", "Standard user with basic permissions", 40),

  /** Guest user with limited access. */
  GUEST("Guest", "Limited access for external users", 20);

  private final String displayName;
  private final String description;
  private final int priority;

  /**
   * Constructor for UserRole enum.
   *
   * @param displayName human-readable display name
   * @param description detailed description of the role
   * @param priority priority level for role hierarchy
   */
  UserRole(String displayName, String description, int priority) {
    this.displayName = displayName;
    this.description = description;
    this.priority = priority;
  }

  /**
   * Gets the human-readable display name of the role.
   *
   * @return display name
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Gets the detailed description of the role.
   *
   * @return role description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the priority level of the role.
   *
   * @return priority level
   */
  public int getPriority() {
    return priority;
  }

  /**
   * Checks if this role has higher or equal priority than the given role.
   *
   * @param otherRole the role to compare with
   * @return true if this role has higher or equal priority
   */
  public boolean hasHigherOrEqualPriority(UserRole otherRole) {
    return this.priority >= otherRole.priority;
  }

  /**
   * Checks if this role has higher priority than the given role.
   *
   * @param otherRole the role to compare with
   * @return true if this role has higher priority
   */
  public boolean hasHigherPriority(UserRole otherRole) {
    return this.priority > otherRole.priority;
  }

  /**
   * Checks if this role can manage the given role.
   *
   * @param targetRole the role to check if it can be managed
   * @return true if this role can manage the target role
   */
  public boolean canManage(UserRole targetRole) {
    return this.priority > targetRole.priority;
  }

  /**
   * Checks if this role is an administrative role.
   *
   * @return true if role is admin or super admin
   */
  public boolean isAdmin() {
    return this == ADMIN || this == SUPER_ADMIN;
  }

  /**
   * Checks if this role is a management role.
   *
   * @return true if role is manager or higher
   */
  public boolean isManager() {
    return this.priority >= MANAGER.priority;
  }

  /**
   * Gets all roles that this role can manage.
   *
   * @return set of roles that can be managed
   */
  public Set<UserRole> getManageableRoles() {
    return Set.of(values()).stream()
        .filter(this::canManage)
        .collect(java.util.stream.Collectors.toSet());
  }

  /**
   * Gets the role from its display name.
   *
   * @param displayName the display name to search for
   * @return the matching UserRole or null if not found
   */
  public static UserRole fromDisplayName(String displayName) {
    for (UserRole role : values()) {
      if (role.displayName.equalsIgnoreCase(displayName)) {
        return role;
      }
    }
    return null;
  }

  /**
   * Gets the role from its name.
   *
   * @param name the role name to search for
   * @return the matching UserRole or null if not found
   */
  public static UserRole fromName(String name) {
    try {
      return UserRole.valueOf(name.toUpperCase());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  @Override
  public String toString() {
    return displayName;
  }
}
