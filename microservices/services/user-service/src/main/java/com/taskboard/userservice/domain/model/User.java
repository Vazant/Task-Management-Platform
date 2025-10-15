package com.taskboard.userservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
@EqualsAndHashCode(of = {"username", "email"}) // Use only business keys
@ToString(exclude = {"password"}) // Exclude sensitive data
public class User {

  private Long id;
  private String username;
  private String email;
  private String password;
  private String firstName;
  private String lastName;
  @Builder.Default
  private UserStatus status = UserStatus.ACTIVE;
  private UserRole role;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime lastLoginAt;
  @Builder.Default
  private boolean emailVerified = false;
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
    // Validate required fields
    if (username == null || username.trim().isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    if (email == null || email.trim().isEmpty()) {
      throw new IllegalArgumentException("Email cannot be null or empty");
    }
    if (!email.contains("@")) {
      throw new IllegalArgumentException("Email must be a valid email address");
    }
    if (password == null || password.trim().isEmpty()) {
      throw new IllegalArgumentException("Password cannot be null or empty");
    }
    if (role == null) {
      throw new IllegalArgumentException("Role cannot be null");
    }
    
    this.username = username.trim();
    this.email = email.trim();
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
    this.role = role;

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
   * If both first and last names are available, returns full name.
   * Otherwise, returns username.
   *
   * @return full name or username if names are not available
   */
  public String getFullName() {
    if (firstName != null && lastName != null && 
        !firstName.trim().isEmpty() && !lastName.trim().isEmpty()) {
      return firstName + " " + lastName;
    }
    return username != null ? username : "Unknown User";
  }

  /**
   * Gets user's display name.
   * If both first and last names are available, returns full name.
   * Otherwise, returns username.
   *
   * @return display name for the user
   */
    public String getDisplayName() {
        if (firstName != null && lastName != null && 
            !firstName.trim().isEmpty() && !lastName.trim().isEmpty()) {
            return firstName + " " + lastName;
        }
        if (firstName != null && !firstName.trim().isEmpty()) {
            return firstName;
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            return lastName;
        }
        return username != null ? username : "Unknown User";
    }

  /**
   * Updates user's profile image URL.
   *
   * @param profileImageUrl new profile image URL
   */
  public void updateProfileImageUrl(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
    this.updatedAt = LocalDateTime.now();
  }

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

  /**
   * Validates the user object and throws exceptions for invalid data.
   * This method should be called after creating a User object to ensure data integrity.
   */
  public void validate() {
    if (username == null || username.trim().isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    if (!isValidUsername(username)) {
      throw new IllegalArgumentException("Username must be at least 3 characters long and contain only alphanumeric characters");
    }
    if (email == null || email.trim().isEmpty()) {
      throw new IllegalArgumentException("Email cannot be null or empty");
    }
    if (!isValidEmail(email)) {
      throw new IllegalArgumentException("Email must be a valid email address");
    }
    if (password == null || password.trim().isEmpty()) {
      throw new IllegalArgumentException("Password cannot be null or empty");
    }
    if (role == null) {
      throw new IllegalArgumentException("Role cannot be null");
    }
  }

  /**
   * Validates username format.
   * 
   * @param username the username to validate
   * @return true if username is valid, false otherwise
   */
  private boolean isValidUsername(String username) {
    if (username == null || username.trim().isEmpty()) {
      return false;
    }
    
    String trimmedUsername = username.trim();
    
    // Username must be at least 3 characters long
    if (trimmedUsername.length() < 3) {
      return false;
    }
    
    // Username can only contain alphanumeric characters
    return trimmedUsername.matches("^[a-zA-Z0-9]+$");
  }

  /**
   * Validates email format.
   * 
   * @param email the email to validate
   * @return true if email is valid, false otherwise
   */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Basic email validation - must contain @ and have valid format
        String trimmedEmail = email.trim();
        if (!trimmedEmail.contains("@")) {
            return false;
        }
        
        String[] parts = trimmedEmail.split("@");
        if (parts.length != 2) {
            return false;
        }
        
        String localPart = parts[0];
        String domainPart = parts[1];
        
        // Local part cannot be empty
        if (localPart.isEmpty()) {
            return false;
        }
        
        // Check for consecutive dots in local part
        if (localPart.contains("..")) {
            return false;
        }
        
        // Domain part cannot be empty and must contain at least one dot
        if (domainPart.isEmpty() || !domainPart.contains(".")) {
            return false;
        }
        
        // Domain part cannot start or end with dot
        if (domainPart.startsWith(".") || domainPart.endsWith(".")) {
            return false;
        }
        
        // Additional validation for common invalid patterns
        if (trimmedEmail.endsWith("@") || trimmedEmail.startsWith("@")) {
            return false;
        }
        
        // Check for multiple @ symbols
        return trimmedEmail.indexOf("@") == trimmedEmail.lastIndexOf("@");
    }


}
