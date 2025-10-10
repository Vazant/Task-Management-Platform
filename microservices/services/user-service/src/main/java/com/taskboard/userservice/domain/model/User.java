package com.taskboard.userservice.domain.model;

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
public class User {

  private Long id;
  private String username;
  private String email;
  private String passwordHash;
  private String firstName;
  private String lastName;
  private UserStatus status;
  private UserRole role;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime lastLoginAt;
  private boolean emailVerified;
  private String profileImageUrl;

  /** Default constructor for JPA and frameworks. */
  protected User() {
    // Required by JPA
  }

  /**
   * Creates a new User with required fields.
   *
   * @param username unique username
   * @param email user's email address
   * @param passwordHash hashed password
   * @param firstName user's first name
   * @param lastName user's last name
   * @param role user's role in the system
   */
  public User(
      String username,
      String email,
      String passwordHash,
      String firstName,
      String lastName,
      UserRole role) {
    this.username = Objects.requireNonNull(username, "Username cannot be null");
    this.email = Objects.requireNonNull(email, "Email cannot be null");
    this.passwordHash = Objects.requireNonNull(passwordHash, "Password hash cannot be null");
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
   * @param newPasswordHash new hashed password
   */
  public void changePassword(String newPasswordHash) {
    this.passwordHash = Objects.requireNonNull(newPasswordHash, "Password hash cannot be null");
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

  // Getters
  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public UserStatus getStatus() {
    return status;
  }

  public UserRole getRole() {
    return role;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public LocalDateTime getLastLoginAt() {
    return lastLoginAt;
  }

  public boolean isEmailVerified() {
    return emailVerified;
  }

  public String getProfileImageUrl() {
    return profileImageUrl;
  }

  // Setters for JPA
  protected void setId(Long id) {
    this.id = id;
  }

  protected void setUsername(String username) {
    this.username = username;
  }

  protected void setEmail(String email) {
    this.email = email;
  }

  protected void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  protected void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  protected void setLastName(String lastName) {
    this.lastName = lastName;
  }

  protected void setStatus(UserStatus status) {
    this.status = status;
  }

  protected void setRole(UserRole role) {
    this.role = role;
  }

  protected void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  protected void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  protected void setLastLoginAt(LocalDateTime lastLoginAt) {
    this.lastLoginAt = lastLoginAt;
  }

  protected void setEmailVerified(boolean emailVerified) {
    this.emailVerified = emailVerified;
  }

  protected void setProfileImageUrl(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(id, user.id) && Objects.equals(username, user.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username);
  }

  @Override
  public String toString() {
    return "User{"
        + "id="
        + id
        + ", username='"
        + username
        + '\''
        + ", email='"
        + email
        + '\''
        + ", firstName='"
        + firstName
        + '\''
        + ", lastName='"
        + lastName
        + '\''
        + ", status="
        + status
        + ", role="
        + role
        + ", createdAt="
        + createdAt
        + '}';
  }
}
