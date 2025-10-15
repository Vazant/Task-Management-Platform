package com.taskboard.userservice.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Test data builder for User domain model.
 * Provides fluent API for creating test users with different configurations.
 */
public class UserTestDataBuilder {
    
    private Long id = 1L;
    private String username = "testuser";
    private String email = "test@example.com";
    private String password = "password123";
    private String firstName = "Test";
    private String lastName = "User";
    private UserRole role = UserRole.USER;
    private UserStatus status = UserStatus.ACTIVE;
    private String profileImageUrl = "https://example.com/avatar.jpg";
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String externalId = UUID.randomUUID().toString();
    
    public static UserTestDataBuilder aUser() {
        return new UserTestDataBuilder();
    }
    
    public UserTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }
    
    public UserTestDataBuilder withUsername(String username) {
        this.username = username;
        return this;
    }
    
    public UserTestDataBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
    
    public UserTestDataBuilder withPassword(String password) {
        this.password = password;
        return this;
    }
    
    public UserTestDataBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }
    
    public UserTestDataBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
    
    public UserTestDataBuilder withRole(UserRole role) {
        this.role = role;
        return this;
    }
    
    public UserTestDataBuilder withStatus(UserStatus status) {
        this.status = status;
        return this;
    }
    
    public UserTestDataBuilder withProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        return this;
    }
    
    public UserTestDataBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }
    
    public UserTestDataBuilder withUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
    
    public UserTestDataBuilder withExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }
    
    public User build() {
        User user = User.builder()
            .id(id)
            .username(username)
            .email(email)
            .password(password)
            .firstName(firstName)
            .lastName(lastName)
            .role(role)
            .status(status)
            .profileImageUrl(profileImageUrl)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .externalId(externalId)
            .build();
        
        // Set timestamps if not provided
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        if (user.getUpdatedAt() == null) {
            user.setUpdatedAt(LocalDateTime.now());
        }
        
        return user;
    }
}
