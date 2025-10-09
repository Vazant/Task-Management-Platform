package com.taskboard.userservice.domain.repository;

import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.model.UserStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User domain entities.
 * 
 * <p>This interface defines the contract for user data access operations
 * from a domain perspective. It follows the Repository pattern and provides
 * a clean abstraction over data persistence concerns.</p>
 * 
 * <p>The repository is responsible for:</p>
 * <ul>
 *   <li>Persisting user entities</li>
 *   <li>Retrieving users by various criteria</li>
 *   <li>Managing user lifecycle operations</li>
 *   <li>Providing domain-specific query methods</li>
 * </ul>
 * 
 * <p>This interface is part of the domain layer and should not contain
 * any infrastructure-specific details. The actual implementation will
 * be provided by the infrastructure layer.</p>
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
public interface UserRepository {
    
    /**
     * Saves a user entity.
     * 
     * <p>This method persists a user entity to the underlying data store.
     * If the user already exists, it will be updated; otherwise, a new
     * user will be created.</p>
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
     * @return true if the user was deleted, false if not found
     * @throws IllegalArgumentException if id is null
     */
    boolean deleteById(Long id);
    
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
}
