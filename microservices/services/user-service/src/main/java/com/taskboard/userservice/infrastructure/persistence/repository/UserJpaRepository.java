package com.taskboard.userservice.infrastructure.persistence.repository;

import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.model.UserStatus;
import com.taskboard.userservice.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for UserEntity operations.
 *
 * <p>This repository provides data access methods for UserEntity using Spring Data JPA.
 * It includes custom queries for common user operations and follows Spring Data JPA
 * best practices.
 *
 * <p>The UserJpaRepository is responsible for:
 *
 * <ul>
 *   <li>Basic CRUD operations for UserEntity
 *   <li>Custom queries for user search and filtering
 *   <li>Pagination support for large datasets
 *   <li>Performance-optimized queries
 * </ul>
 *
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

  /**
   * Finds a user by username.
   *
   * @param username the username to search for
   * @return Optional containing the user if found
   */
  Optional<UserEntity> findByUsername(String username);

  /**
   * Finds a user by email.
   *
   * @param email the email to search for
   * @return Optional containing the user if found
   */
  Optional<UserEntity> findByEmail(String email);

  /**
   * Checks if a user exists with the given username.
   *
   * @param username the username to check
   * @return true if user exists
   */
  boolean existsByUsername(String username);

  /**
   * Checks if a user exists with the given email.
   *
   * @param email the email to check
   * @return true if user exists
   */
  boolean existsByEmail(String email);

  /**
   * Checks if a user exists with the given email and different ID.
   *
   * @param email the email to check
   * @param id the user ID to exclude
   * @return true if user exists with email and different ID
   */
  boolean existsByEmailAndIdNot(String email, Long id);

  /**
   * Finds users by status.
   *
   * @param status the user status
   * @return list of users with the given status
   */
  List<UserEntity> findByStatus(UserStatus status);

  /**
   * Finds users by role.
   *
   * @param role the user role
   * @return list of users with the given role
   */
  List<UserEntity> findByRole(UserRole role);

  /**
   * Finds users by status with pagination.
   *
   * @param status the user status
   * @param pageable pagination information
   * @return page of users with the given status
   */
  Page<UserEntity> findByStatus(UserStatus status, Pageable pageable);

  /**
   * Finds users by role with pagination.
   *
   * @param role the user role
   * @param pageable pagination information
   * @return page of users with the given role
   */
  Page<UserEntity> findByRole(UserRole role, Pageable pageable);

  /**
   * Finds users created after a specific date.
   *
   * @param date the date to search from
   * @return list of users created after the date
   */
  List<UserEntity> findByCreatedAtAfter(LocalDateTime date);

  /**
   * Finds users created between two dates.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return list of users created between the dates
   */
  List<UserEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

  /**
   * Finds users by first name containing the given text (case-insensitive).
   *
   * @param firstName the text to search for in first name
   * @return list of users with matching first name
   */
  List<UserEntity> findByFirstNameContainingIgnoreCase(String firstName);

  /**
   * Finds users by last name containing the given text (case-insensitive).
   *
   * @param lastName the text to search for in last name
   * @return list of users with matching last name
   */
  List<UserEntity> findByLastNameContainingIgnoreCase(String lastName);

  /**
   * Finds users by full name containing the given text (case-insensitive).
   *
   * @param searchText the text to search for in first or last name
   * @return list of users with matching name
   */
  @Query(
      "SELECT u FROM UserEntity u WHERE "
          + "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR "
          + "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchText, '%'))")
  List<UserEntity> findByFullNameContainingIgnoreCase(@Param("searchText") String searchText);

  /**
   * Finds users by multiple criteria with pagination.
   *
   * @param status the user status (optional)
   * @param role the user role (optional)
   * @param searchText the text to search in names (optional)
   * @param pageable pagination information
   * @return page of users matching the criteria
   */
  @Query(
      "SELECT u FROM UserEntity u WHERE "
          + "(:status IS NULL OR u.status = :status) AND "
          + "(:role IS NULL OR u.role = :role) AND "
          + "(:searchText IS NULL OR "
          + "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR "
          + "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR "
          + "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchText, '%')))")
  Page<UserEntity> findByCriteria(
      @Param("status") UserStatus status,
      @Param("role") UserRole role,
      @Param("searchText") String searchText,
      Pageable pageable);

  /**
   * Counts users by status.
   *
   * @param status the user status
   * @return number of users with the given status
   */
  long countByStatus(UserStatus status);

  /**
   * Counts users by role.
   *
   * @param role the user role
   * @return number of users with the given role
   */
  long countByRole(UserRole role);

  /**
   * Finds users who haven't logged in since a specific date.
   *
   * @param date the date to check against
   * @return list of users who haven't logged in since the date
   */
  @Query(
      "SELECT u FROM UserEntity u WHERE "
          + "u.lastLoginAt IS NULL OR u.lastLoginAt < :date")
  List<UserEntity> findUsersNotLoggedInSince(@Param("date") LocalDateTime date);

  /**
   * Finds users with unverified email.
   *
   * @return list of users with unverified email
   */
  List<UserEntity> findByEmailVerifiedFalse();

  /**
   * Finds users with verified email.
   *
   * @return list of users with verified email
   */
  List<UserEntity> findByEmailVerifiedTrue();

  /**
   * Deletes users by status (for cleanup operations).
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
  @Query("UPDATE UserEntity u SET u.lastLoginAt = :loginTime WHERE u.id = :userId")
  void updateLastLoginTime(@Param("userId") Long userId, @Param("loginTime") LocalDateTime loginTime);

  /**
   * Updates user's email verification status.
   *
   * @param userId the user ID
   * @param verified the verification status
   */
  @Query("UPDATE UserEntity u SET u.emailVerified = :verified WHERE u.id = :userId")
  void updateEmailVerificationStatus(@Param("userId") Long userId, @Param("verified") boolean verified);
}
