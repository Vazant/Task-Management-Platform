package com.taskboard.userservice.application.dto;

import com.taskboard.userservice.domain.model.User;
import com.taskboard.userservice.domain.model.UserRole;
import com.taskboard.userservice.domain.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for User entity.
 * 
 * <p>This DTO represents a user in the system and is used for data transfer
 * between different layers of the application. It contains all the necessary
 * user information without sensitive data like passwords.
 * 
 * <p>Example usage:
 * 
 * <pre>{@code
 * UserDto userDto = UserDto.builder()
 *     .id(1L)
 *     .username("johndoe")
 *     .email("john@example.com")
 *     .firstName("John")
 *     .lastName("Doe")
 *     .role(UserRole.USER)
 *     .status(UserStatus.ACTIVE)
 *     .build();
 * }</pre>
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 * @see User
 * @see UserRole
 * @see UserStatus
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    private boolean emailVerified;
    private String profileImageUrl;
    
    /**
     * Creates a UserDto from a User domain entity.
     * 
     * @param user the user domain entity
     * @return the corresponding UserDto
     */
    public static UserDto fromUser(User user) {
        return UserDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(user.getRole())
            .status(user.getStatus())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .lastLoginAt(user.getLastLoginAt())
            .emailVerified(user.isEmailVerified())
            .profileImageUrl(user.getProfileImageUrl())
            .build();
    }
    
    /**
     * Gets the user's full name.
     * 
     * @return concatenated first and last name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Sets the user's active status.
     * 
     * @param active true if user is active, false otherwise
     */
    public UserDto active(boolean active) {
        this.status = active ? UserStatus.ACTIVE : UserStatus.INACTIVE;
        return this;
    }

}
