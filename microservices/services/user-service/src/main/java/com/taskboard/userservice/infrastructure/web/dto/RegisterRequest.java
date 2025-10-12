package com.taskboard.userservice.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for new user registration.
 * Contains all required information to create a new user account.
 * 
 * <p>This DTO is used for the registration endpoint and includes:
 * <ul>
 *   <li>Username and email for account identification</li>
 *   <li>Password with confirmation for security</li>
 *   <li>Personal information (first name, last name)</li>
 *   <li>Terms acceptance flag</li>
 * </ul>
 * 
 * <p>All fields are validated according to security and business requirements.
 * 
 * @author TaskBoard Team
 * @version 1.0
 * @since 1.0.0
 * @see RegisterResponse
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    /**
     * Unique username for the account.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, underscores and hyphens")
    private String username;
    
    /**
     * Email address for the account.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    /**
     * Password for the account.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
    
    /**
     * Password confirmation to ensure accuracy.
     */
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
    
    /**
     * User's first name.
     */
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    /**
     * User's last name.
     */
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    /**
     * Flag indicating acceptance of terms and conditions.
     */
    @Builder.Default
    private boolean acceptTerms = false;
}
