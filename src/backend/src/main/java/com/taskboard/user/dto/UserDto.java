package com.taskboard.user.dto;

import com.taskboard.user.model.UserRole;
import lombok.*;

import java.util.Date;

/**
 * DTO representing user data for API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    
    private String id;
    private String email;
    private String username;
    private UserRole role;
    private String avatar;
    private Date createdAt;
    private Date lastLogin;
    private UserPreferencesDto preferences;
}