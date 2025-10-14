package com.taskboard.userservice.application.dto;

import com.taskboard.userservice.domain.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Request DTO for user registration.
 * Contains all the information required to register a new user.
 * 
 * <p>This DTO is used for the registration endpoint and includes:
 * <ul>
 *   <li>Username for identification</li>
 *   <li>Email address for communication</li>
 *   <li>Password for authentication</li>
 *   <li>Personal information (first name, last name)</li>
 *   <li>User role assignment</li>
 * </ul>
 * 
 * <p>All fields are validated according to security and business requirements.
 * 
 * @author Task Management Platform Team
 * @version 1.0.0
 * @since 1.0.0
 * @see UserRole
 * @see RegisterResponse
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
public class RegisterRequest {
    
    /**
     * Unique username for the user.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, underscores, and hyphens")
    private String username;
    
    /**
     * User's email address.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    /**
     * User's password.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password;
    
    /**
     * User's first name.
     */
    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;
    
    /**
     * User's last name.
     */
    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;
    
    /**
     * User's role in the system.
     */
    @NotNull(message = "Role is required")
    private UserRole role;
    
    /**
     * Optional profile image URL.
     */
    @Pattern(
        regexp = "^(?:https?://)?(?:[A-Za-z0-9-]++\\.)++[A-Za-z]{2,63}(?:/[\\w.-]++)*+/?$",
        message = "Profile image URL must be a valid URL"
    )
    private String profileImageUrl;
}


