package com.taskboard.userservice.infrastructure.persistence.mapper;

import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.model.UserStatus;
import com.taskboard.userservice.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between User domain model and UserEntity JPA entity.
 *
 * <p>This mapper handles the conversion between the domain layer User model and the
 * infrastructure layer UserEntity. It ensures proper separation of concerns and
 * maintains the integrity of both models.
 *
 * <p>The UserEntityMapper is responsible for:
 *
 * <ul>
 *   <li>Converting User domain model to UserEntity
 *   <li>Converting UserEntity to User domain model
 *   <li>Handling null values and edge cases
 *   <li>Maintaining data integrity during conversion
 * </ul>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class UserEntityMapper {

  /**
   * Converts UserEntity to User domain model.
   *
   * @param entity the UserEntity to convert
   * @return the User domain model
   * @throws IllegalArgumentException if entity is null
   */
  public User toDomain(UserEntity entity) {
    if (entity == null) {
      throw new IllegalArgumentException("UserEntity cannot be null");
    }

    User user = new User(
        entity.getUsername(),
        entity.getEmail(),
        entity.getPasswordHash(),
        entity.getFirstName(),
        entity.getLastName(),
        entity.getRole());

    // Set additional fields using reflection or setters
    setDomainFields(user, entity);

    return user;
  }

  /**
   * Converts User domain model to UserEntity.
   *
   * @param user the User domain model to convert
   * @return the UserEntity
   * @throws IllegalArgumentException if user is null
   */
  public UserEntity toEntity(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }

    UserEntity entity = new UserEntity(
        user.getUsername(),
        user.getEmail(),
        user.getPasswordHash(),
        user.getFirstName(),
        user.getLastName(),
        user.getRole());

    // Set additional fields
    setEntityFields(entity, user);

    return entity;
  }

  /**
   * Converts a list of UserEntity to a list of User domain models.
   *
   * @param entities the list of UserEntity to convert
   * @return the list of User domain models
   */
  public List<User> toDomainList(List<UserEntity> entities) {
    if (entities == null) {
      return List.of();
    }

    return entities.stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  /**
   * Converts a list of User domain models to a list of UserEntity.
   *
   * @param users the list of User domain models to convert
   * @return the list of UserEntity
   */
  public List<UserEntity> toEntityList(List<User> users) {
    if (users == null) {
      return List.of();
    }

    return users.stream()
        .map(this::toEntity)
        .collect(Collectors.toList());
  }

  /**
   * Updates an existing UserEntity with data from User domain model.
   *
   * @param entity the existing UserEntity to update
   * @param user the User domain model with new data
   * @throws IllegalArgumentException if either parameter is null
   */
  public void updateEntity(UserEntity entity, User user) {
    if (entity == null) {
      throw new IllegalArgumentException("UserEntity cannot be null");
    }
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }

    // Update basic fields
    entity.setUsername(user.getUsername());
    entity.setEmail(user.getEmail());
    entity.setPasswordHash(user.getPasswordHash());
    entity.setFirstName(user.getFirstName());
    entity.setLastName(user.getLastName());
    entity.setStatus(user.getStatus());
    entity.setRole(user.getRole());
    entity.setLastLoginAt(user.getLastLoginAt());
    entity.setEmailVerified(user.isEmailVerified());
    entity.setProfileImageUrl(user.getProfileImageUrl());
  }

  /**
   * Sets additional fields on the User domain model from UserEntity.
   *
   * @param user the User domain model
   * @param entity the UserEntity
   */
  private void setDomainFields(User user, UserEntity entity) {
    // Use reflection to set private fields or create a builder pattern
    try {
      // Set ID
      var idField = User.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(user, entity.getId());

      // Set status
      var statusField = User.class.getDeclaredField("status");
      statusField.setAccessible(true);
      statusField.set(user, entity.getStatus());

      // Set timestamps
      var createdAtField = User.class.getDeclaredField("createdAt");
      createdAtField.setAccessible(true);
      createdAtField.set(user, entity.getCreatedAt());

      var updatedAtField = User.class.getDeclaredField("updatedAt");
      updatedAtField.setAccessible(true);
      updatedAtField.set(user, entity.getUpdatedAt());

      var lastLoginAtField = User.class.getDeclaredField("lastLoginAt");
      lastLoginAtField.setAccessible(true);
      lastLoginAtField.set(user, entity.getLastLoginAt());

      // Set email verification
      var emailVerifiedField = User.class.getDeclaredField("emailVerified");
      emailVerifiedField.setAccessible(true);
      emailVerifiedField.set(user, entity.isEmailVerified());

      // Set profile image URL
      var profileImageUrlField = User.class.getDeclaredField("profileImageUrl");
      profileImageUrlField.setAccessible(true);
      profileImageUrlField.set(user, entity.getProfileImageUrl());

    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException("Failed to set domain fields", e);
    }
  }

  /**
   * Sets additional fields on the UserEntity from User domain model.
   *
   * @param entity the UserEntity
   * @param user the User domain model
   */
  private void setEntityFields(UserEntity entity, User user) {
    // Set ID if available
    if (user.getId() != null) {
      entity.setId(user.getId());
    }

    // Set status
    entity.setStatus(user.getStatus());

    // Set timestamps
    entity.setCreatedAt(user.getCreatedAt());
    entity.setUpdatedAt(user.getUpdatedAt());
    entity.setLastLoginAt(user.getLastLoginAt());

    // Set email verification
    entity.setEmailVerified(user.isEmailVerified());

    // Set profile image URL
    entity.setProfileImageUrl(user.getProfileImageUrl());
  }

  /**
   * Creates a copy of UserEntity with updated fields.
   *
   * @param original the original UserEntity
   * @param updates the User with updated fields
   * @return a new UserEntity with updated fields
   */
  public UserEntity createUpdatedEntity(UserEntity original, User updates) {
    UserEntity updated = new UserEntity(
        updates.getUsername(),
        updates.getEmail(),
        updates.getPasswordHash(),
        updates.getFirstName(),
        updates.getLastName(),
        updates.getRole());

    // Preserve original ID and timestamps
    updated.setId(original.getId());
    updated.setCreatedAt(original.getCreatedAt());
    updated.setVersion(original.getVersion());

    // Set updated fields
    updated.setStatus(updates.getStatus());
    updated.setLastLoginAt(updates.getLastLoginAt());
    updated.setEmailVerified(updates.isEmailVerified());
    updated.setProfileImageUrl(updates.getProfileImageUrl());

    return updated;
  }

  /**
   * Validates that a UserEntity can be converted to User domain model.
   *
   * @param entity the UserEntity to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidForDomainConversion(UserEntity entity) {
    if (entity == null) {
      return false;
    }

    return entity.getUsername() != null
        && entity.getEmail() != null
        && entity.getPasswordHash() != null
        && entity.getFirstName() != null
        && entity.getLastName() != null
        && entity.getRole() != null
        && entity.getStatus() != null;
  }

  /**
   * Validates that a User domain model can be converted to UserEntity.
   *
   * @param user the User to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidForEntityConversion(User user) {
    if (user == null) {
      return false;
    }

    return user.getUsername() != null
        && user.getEmail() != null
        && user.getPasswordHash() != null
        && user.getFirstName() != null
        && user.getLastName() != null
        && user.getRole() != null
        && user.getStatus() != null;
  }
}
