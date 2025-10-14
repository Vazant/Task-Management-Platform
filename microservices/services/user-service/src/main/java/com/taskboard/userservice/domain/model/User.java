package com.taskboard.userservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a User in the system.
 *
 * <p>This is the core domain entity that encapsulates user business logic and maintains data
 * integrity. It follows Domain-Driven Design principles and contains only business-related
 * behavior.
 *
 * <p>The User entity is responsible for:
 *
 * <ul>
 *   <li>Maintaining user identity and profile information
 *   <li>Managing user status and roles
 *   <li>Validating business rules
 *   <li>Providing domain-specific behavior
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
public class User {

  private Long id;
  private String username;
  private String email;
  private String password;
  private String firstName;
  private String lastName;
  private UserStatus status;
  private UserRole role;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime lastLoginAt;
  private boolean emailVerified;
  private String profileImageUrl;
  private String externalId;

  /**
   * Creates a new User with required fields.
   *
   * @param username unique username
   * @param email user's email address
   * @param password hashed password
   * @param firstName user's first name
   * @param lastName user's last name
   * @param role user's role in the system
   */
  public User(
      String username,
      String email,
      String password,
      String firstName,
      String lastName,
      UserRole role) {
    this.username = Objects.requireNonNull(username, "Username cannot be null");
    this.email = Objects.requireNonNull(email, "Email cannot be null");
    this.password = Objects.requireNonNull(password, "Password cannot be null");
    this.firstName = Objects.requireNonNull(firstName, "First name cannot be null");
    this.lastName = Objects.requireNonNull(lastName, "Last name cannot be null");
    this.role = Objects.requireNonNull(role, "Role cannot be null");

    this.status = UserStatus.ACTIVE;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.emailVerified = false;
  }

  /**
   * Updates user's basic information.
   *
   * @param firstName new first name
   * @param lastName new last name
   * @param email new email address
   */
  public void updateProfile(String firstName, String lastName, String email) {
    this.firstName = Objects.requireNonNull(firstName, "First name cannot be null");
    this.lastName = Objects.requireNonNull(lastName, "Last name cannot be null");
    this.email = Objects.requireNonNull(email, "Email cannot be null");
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Changes user's password.
   *
   * @param newPassword new hashed password
   */
  public void changePassword(String newPassword) {
    this.password = Objects.requireNonNull(newPassword, "Password cannot be null");
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Updates user's role.
   *
   * @param newRole new user role
   */
  public void updateRole(UserRole newRole) {
    this.role = Objects.requireNonNull(newRole, "Role cannot be null");
    this.updatedAt = LocalDateTime.now();
  }

  /** Activates the user account. */
  public void activate() {
    this.status = UserStatus.ACTIVE;
    this.updatedAt = LocalDateTime.now();
  }

  /** Deactivates the user account. */
  public void deactivate() {
    this.status = UserStatus.INACTIVE;
    this.updatedAt = LocalDateTime.now();
  }

  /** Blocks the user account. */
  public void block() {
    this.status = UserStatus.BLOCKED;
    this.updatedAt = LocalDateTime.now();
  }

  /** Records user login. */
  public void recordLogin() {
    this.lastLoginAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Updates the last login time.
   *
   * @param lastLoginAt the new last login time
   */
  public void updateLastLogin(LocalDateTime lastLoginAt) {
    this.lastLoginAt = lastLoginAt;
    this.updatedAt = LocalDateTime.now();
  }

  /** Marks email as verified. */
  public void verifyEmail() {
    this.emailVerified = true;
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Checks if user is active.
   *
   * @return true if user is active
   */
  public boolean isActive() {
    return status == UserStatus.ACTIVE;
  }

  /**
   * Checks if user is blocked.
   *
   * @return true if user is blocked
   */
  public boolean isBlocked() {
    return status == UserStatus.BLOCKED;
  }

  /**
   * Checks if user has admin role.
   *
   * @return true if user is admin
   */
  public boolean isAdmin() {
    return role == UserRole.ADMIN;
  }

  /**
   * Gets user's full name.
   *
   * @return concatenated first and last name
   */
  public String getFullName() {
    return firstName + " " + lastName;
  }

  // Lombok @Data annotation provides getters and setters automatically

  /**
   * Updates user's profile image URL.
   *
   * @param profileImageUrl new profile image URL
   */
  public void updateProfileImageUrl(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
    this.updatedAt = LocalDateTime.now();
  }

  // Lombok @Data annotation provides equals, hashCode, and toString automatically

  /**
   * Updates the user's email address.
   *
   * @param email the new email address
   */
  public void updateEmail(String email) {
    this.email = email;
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Updates the user's first name.
   *
   * @param firstName the new first name
   */
  public void updateFirstName(String firstName) {
    this.firstName = firstName;
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Updates the user's last name.
   *
   * @param lastName the new last name
   */
  public void updateLastName(String lastName) {
    this.lastName = lastName;
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Creates a copy of this user with updated values.
   * 
   * @return a new builder initialized with current values
   */
  public UserBuilder toBuilder() {
    return User.builder()
        .id(id)
        .username(username)
        .email(email)
        .password(password)
        .firstName(firstName)
        .lastName(lastName)
        .status(status)
        .role(role)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .lastLoginAt(lastLoginAt)
        .emailVerified(emailVerified)
        .profileImageUrl(profileImageUrl)
        .externalId(externalId);
  }

}
