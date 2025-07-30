package com.taskboard.user.repository;

import com.taskboard.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

/**
 * Repository interface for UserEntity.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    /**
     * Find a user by username.
     *
     * @param username the username
     * @return Optional containing the user if found
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Find a user by email.
     *
     * @param email the email
     * @return Optional containing the user if found
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Check if a user exists by username.
     *
     * @param username the username
     * @return true if user exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if a user exists by email.
     *
     * @param email the email
     * @return true if user exists
     */
    boolean existsByEmail(String email);

    /**
     * Find a user by password reset token.
     *
     * @param token the password reset token
     * @return Optional containing the user if found
     */
    Optional<UserEntity> findByPasswordResetToken(String token);

    /**
     * Update last login timestamp for a user.
     *
     * @param userId the user ID
     * @param lastLogin the last login date
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.lastLogin = :lastLogin WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") String userId, @Param("lastLogin") Date lastLogin);

    /**
     * Delete users with expired password reset tokens.
     *
     * @param currentDate the current date
     * @return number of deleted records
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.passwordResetToken = null, u.passwordResetTokenExpiry = null " +
           "WHERE u.passwordResetTokenExpiry < :currentDate")
    int clearExpiredPasswordResetTokens(@Param("currentDate") Date currentDate);
}
