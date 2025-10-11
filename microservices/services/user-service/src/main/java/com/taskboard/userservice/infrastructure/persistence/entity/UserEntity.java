package com.taskboard.userservice.infrastructure.persistence.entity;

import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.model.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA Entity representing a User in the database.
 *
 * <p>This entity maps the domain User model to the database table. It follows JPA best practices
 * and includes proper validation, indexing, and auditing.
 *
 * <p>The UserEntity is responsible for:
 *
 * <ul>
 *   <li>Mapping domain User to database table
 *   <li>Providing JPA annotations and constraints
 *   <li>Handling database-specific concerns
 *   <li>Maintaining referential integrity
 * </ul>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(
    name = "users",
    indexes = {
      @Index(name = "idx_user_username", columnList = "username"),
      @Index(name = "idx_user_email", columnList = "email"),
      @Index(name = "idx_user_status", columnList = "status"),
      @Index(name = "idx_user_role", columnList = "role"),
      @Index(name = "idx_user_created_at", columnList = "created_at")
    },
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_user_username", columnNames = "username"),
      @UniqueConstraint(name = "uk_user_email", columnNames = "email")
    })
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  @Column(name = "username", nullable = false, unique = true, length = 50)
  private String username;

  @Email(message = "Email must be valid")
  @NotBlank(message = "Email is required")
  @Size(max = 100, message = "Email cannot exceed 100 characters")
  @Column(name = "email", nullable = false, unique = true, length = 100)
  private String email;

  @NotBlank(message = "Password hash is required")
  @Size(max = 255, message = "Password hash cannot exceed 255 characters")
  @Column(name = "password_hash", nullable = false, length = 255)
  private String passwordHash;

  @NotBlank(message = "First name is required")
  @Size(max = 50, message = "First name cannot exceed 50 characters")
  @Column(name = "first_name", nullable = false, length = 50)
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(max = 50, message = "Last name cannot exceed 50 characters")
  @Column(name = "last_name", nullable = false, length = 50)
  private String lastName;

  @NotNull(message = "Status is required")
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private UserStatus status;

  @NotNull(message = "Role is required")
  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 20)
  private UserRole role;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "last_login_at")
  private LocalDateTime lastLoginAt;

  @Column(name = "email_verified", nullable = false)
  private boolean emailVerified = false;

  @Size(max = 500, message = "Profile image URL cannot exceed 500 characters")
  @Column(name = "profile_image_url", length = 500)
  private String profileImageUrl;

  @Version
  @Column(name = "version")
  private Long version;

  /** Default constructor for JPA. */
  protected UserEntity() {
    // Required by JPA
  }

  /**
   * Creates a new UserEntity with required fields.
   *
   * @param username unique username
   * @param email user's email address
   * @param passwordHash hashed password
   * @param firstName user's first name
   * @param lastName user's last name
   * @param role user's role in the system
   */
  public UserEntity(
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
  }

  /**
   * Changes user's password.
   *
   * @param newPasswordHash new hashed password
   */
  public void changePassword(String newPasswordHash) {
    this.passwordHash = Objects.requireNonNull(newPasswordHash, "Password hash cannot be null");
  }

  /**
   * Updates user's role.
   *
   * @param newRole new user role
   */
  public void updateRole(UserRole newRole) {
    this.role = Objects.requireNonNull(newRole, "Role cannot be null");
  }

  /** Activates the user account. */
  public void activate() {
    this.status = UserStatus.ACTIVE;
  }

  /** Deactivates the user account. */
  public void deactivate() {
    this.status = UserStatus.INACTIVE;
  }

  /** Blocks the user account. */
  public void block() {
    this.status = UserStatus.BLOCKED;
  }

  /** Records user login. */
  public void recordLogin() {
    this.lastLoginAt = LocalDateTime.now();
  }

  /** Marks email as verified. */
  public void verifyEmail() {
    this.emailVerified = true;
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

  /**
   * Updates user's profile image URL.
   *
   * @param profileImageUrl new profile image URL
   */
  public void updateProfileImageUrl(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
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

  public Long getVersion() {
    return version;
  }

  // Setters for JPA and Mapper
  public void setId(Long id) {
    this.id = id;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public void setRole(UserRole role) {
    this.role = role;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public void setLastLoginAt(LocalDateTime lastLoginAt) {
    this.lastLoginAt = lastLoginAt;
  }

  public void setEmailVerified(boolean emailVerified) {
    this.emailVerified = emailVerified;
  }

  public void setProfileImageUrl(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserEntity that = (UserEntity) o;
    return Objects.equals(id, that.id) && Objects.equals(username, that.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username);
  }

  @Override
  public String toString() {
    return "UserEntity{"
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
        + ", version="
        + version
        + '}';
  }
}
