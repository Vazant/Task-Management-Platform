package com.taskboard.userservice.domain.repository;

import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.model.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User domain entities.
 *
 * <p>This interface defines the contract for user data access operations from a domain perspective.
 * It follows the Repository pattern and provides a clean abstraction over data persistence
 * concerns.
 *
 * <p>The repository is responsible for:
 *
 * <ul>
 *   <li>Persisting user entities
 *   <li>Retrieving users by various criteria
 *   <li>Managing user lifecycle operations
 *   <li>Providing domain-specific query methods
 * </ul>
 *
 * <p>This interface is part of the domain layer and should not contain any infrastructure-specific
 * details. The actual implementation will be provided by the infrastructure layer.
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
public interface UserRepository {

  /**
   * Saves a user entity.
   *
   * <p>This method persists a user entity to the underlying data store. If the user already exists,
   * it will be updated; otherwise, a new user will be created.
   *
   * @param user the user entity to save
   * @return the saved user entity
   * @throws IllegalArgumentException if user is null
   */
  User save(User user);

  /**
   * Finds a user by its unique identifier.
   *
   * @param id the user ID
   * @return an Optional containing the user if found, empty otherwise
   * @throws IllegalArgumentException if id is null
   */
  Optional<User> findById(Long id);

  /**
   * Finds a user by username.
   *
   * @param username the username to search for
   * @return an Optional containing the user if found, empty otherwise
   * @throws IllegalArgumentException if username is null or empty
   */
  Optional<User> findByUsername(String username);

  /**
   * Finds a user by email address.
   *
   * @param email the email address to search for
   * @return an Optional containing the user if found, empty otherwise
   * @throws IllegalArgumentException if email is null or empty
   */
  Optional<User> findByEmail(String email);

  /**
   * Finds all users with the specified status.
   *
   * @param status the user status to filter by
   * @return a list of users with the specified status
   * @throws IllegalArgumentException if status is null
   */
  List<User> findByStatus(UserStatus status);

  /**
   * Finds all users with the specified role.
   *
   * @param role the user role to filter by
   * @return a list of users with the specified role
   * @throws IllegalArgumentException if role is null
   */
  List<User> findByRole(UserRole role);

  /**
   * Finds all users.
   *
   * @return a list of all users in the system
   */
  List<User> findAll();

  /**
   * Checks if a user exists with the specified username.
   *
   * @param username the username to check
   * @return true if a user exists with the username, false otherwise
   * @throws IllegalArgumentException if username is null or empty
   */
  boolean existsByUsername(String username);

  /**
   * Checks if a user exists with the specified email.
   *
   * @param email the email to check
   * @return true if a user exists with the email, false otherwise
   * @throws IllegalArgumentException if email is null or empty
   */
  boolean existsByEmail(String email);

  /**
   * Deletes a user by its unique identifier.
   *
   * @param id the user ID to delete
   * @throws IllegalArgumentException if id is null
   */
  void deleteById(Long id);

  /**
   * Deletes a user entity.
   *
   * @param user the user entity to delete
   * @throws IllegalArgumentException if user is null
   */
  void delete(User user);

  /**
   * Counts the total number of users.
   *
   * @return the total number of users
   */
  long count();

  /**
   * Counts the number of users with the specified status.
   *
   * @param status the user status to count
   * @return the number of users with the specified status
   * @throws IllegalArgumentException if status is null
   */
  long countByStatus(UserStatus status);

  /**
   * Counts the number of users with the specified role.
   *
   * @param role the user role to count
   * @return the number of users with the specified role
   * @throws IllegalArgumentException if role is null
   */
  long countByRole(UserRole role);

  /**
   * Finds all users with pagination.
   *
   * @param pageable pagination information
   * @return a page of users
   */
  Page<User> findAll(Pageable pageable);

  /**
   * Finds all users with the specified status and pagination.
   *
   * @param status the user status to filter by
   * @param pageable pagination information
   * @return a page of users with the specified status
   */
  Page<User> findByStatus(UserStatus status, Pageable pageable);

  /**
   * Finds all users with the specified role and pagination.
   *
   * @param role the user role to filter by
   * @param pageable pagination information
   * @return a page of users with the specified role
   */
  Page<User> findByRole(UserRole role, Pageable pageable);

  /**
   * Finds users by full name containing the given text.
   *
   * @param searchText the text to search for in first or last name
   * @return list of users with matching name
   */
  List<User> findByFullNameContaining(String searchText);

  /**
   * Finds users by multiple criteria with pagination.
   *
   * @param status the user status (optional)
   * @param role the user role (optional)
   * @param searchText the text to search in names (optional)
   * @param pageable pagination information
   * @return page of users matching the criteria
   */
  Page<User> findByCriteria(UserStatus status, UserRole role, String searchText, Pageable pageable);

  /**
   * Finds users who haven't logged in since a specific date.
   *
   * @param date the date to check against
   * @return list of users who haven't logged in since the date
   */
  List<User> findUsersNotLoggedInSince(LocalDateTime date);

  /**
   * Finds users by email verification status.
   *
   * @param verified the email verification status
   * @return list of users with the specified verification status
   */
  List<User> findByEmailVerified(boolean verified);

  /**
   * Saves multiple users.
   *
   * @param users the list of users to save
   * @return the list of saved users
   */
  List<User> saveAll(List<User> users);

  /**
   * Deletes multiple users.
   *
   * @param users the list of users to delete
   */
  void deleteAll(List<User> users);

  /**
   * Deletes users by status.
   *
   * @param status the user status
   * @return number of deleted users
   */
  long deleteByStatus(UserStatus status);

  /**
   * Updates user's last login time.
   *
   * @param userId the user ID
   * @param loginTime the login time
   */
  void updateLastLoginTime(Long userId, LocalDateTime loginTime);

  /**
   * Updates user's email verification status.
   *
   * @param userId the user ID
   * @param verified the verification status
   */
  void updateEmailVerificationStatus(Long userId, boolean verified);

  /**
   * Checks if a user exists with the specified ID.
   *
   * @param id the user ID to check
   * @return true if a user exists with the ID, false otherwise
   */
  boolean existsById(Long id);

  /**
   * Checks if a user exists with the specified email and different ID.
   *
   * @param email the email to check
   * @param id the user ID to exclude
   * @return true if a user exists with the email and different ID, false otherwise
   */
  boolean existsByEmailAndIdNot(String email, Long id);
}
