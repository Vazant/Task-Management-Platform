package com.taskboard.user.dto;

import lombok.*;

import java.util.Date;

/**
 * DTO representing user profile data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDto {
    
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String avatar;
    private String role;
    private Date lastLogin;
    private Date createdAt;
    private Date updatedAt;
}