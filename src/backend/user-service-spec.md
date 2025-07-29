# User Service Specification

## Overview
This document outlines the specification for the User Service implementation based on the frontend requirements analysis. The service will handle user authentication, registration, profile management, and password management.

## API Endpoints

### Authentication Endpoints

#### 1. Login
- **Method**: POST
- **Path**: `/api/auth/login`
- **Description**: Authenticates a user and returns JWT tokens
- **Request Body**:
  ```json
  {
    "email": "string",
    "password": "string"
  }
  ```
- **Response**:
  ```json
  {
    "user": {
      "id": "string",
      "email": "string",
      "username": "string",
      "role": "user | admin",
      "avatar": "string (optional)",
      "createdAt": "Date",
      "lastLogin": "Date",
      "preferences": {
        "theme": "light | dark",
        "language": "string",
        "notifications": {
          "email": "boolean",
          "push": "boolean",
          "taskUpdates": "boolean",
          "projectUpdates": "boolean"
        }
      }
    },
    "token": "string",
    "refreshToken": "string"
  }
  ```
- **Security**: Public

#### 2. Register
- **Method**: POST
- **Path**: `/api/auth/register`
- **Description**: Registers a new user account
- **Request Body**:
  ```json
  {
    "email": "string",
    "username": "string",
    "password": "string",
    "confirmPassword": "string"
  }
  ```
- **Response**: Same as Login response
- **Security**: Public

#### 3. Refresh Token
- **Method**: POST
- **Path**: `/api/auth/refresh`
- **Description**: Refreshes JWT tokens using a refresh token
- **Request Body**:
  ```json
  {
    "refreshToken": "string"
  }
  ```
- **Response**:
  ```json
  {
    "token": "string",
    "refreshToken": "string"
  }
  ```
- **Security**: Public

#### 4. Forgot Password
- **Method**: POST
- **Path**: `/api/auth/forgot-password`
- **Description**: Initiates password reset process
- **Request Body**:
  ```json
  {
    "email": "string"
  }
  ```
- **Response**:
  ```json
  {
    "message": "string"
  }
  ```
- **Security**: Public

#### 5. Reset Password
- **Method**: POST
- **Path**: `/api/auth/reset-password`
- **Description**: Resets password using a token
- **Request Body**:
  ```json
  {
    "token": "string",
    "password": "string"
  }
  ```
- **Response**:
  ```json
  {
    "message": "string"
  }
  ```
- **Security**: Public

### Profile Management Endpoints

#### 6. Get Profile
- **Method**: GET
- **Path**: `/api/profile`
- **Description**: Retrieves the current user's profile
- **Response**:
  ```json
  {
    "id": "string",
    "username": "string",
    "email": "string",
    "firstName": "string (optional)",
    "lastName": "string (optional)",
    "avatar": "string (optional)",
    "role": "string (optional)",
    "lastLogin": "Date (optional)",
    "createdAt": "Date",
    "updatedAt": "Date"
  }
  ```
- **Security**: Authenticated (JWT required)

#### 7. Update Profile
- **Method**: PUT
- **Path**: `/api/profile`
- **Description**: Updates user profile information
- **Request Body**:
  ```json
  {
    "username": "string",
    "email": "string",
    "firstName": "string (optional)",
    "lastName": "string (optional)"
  }
  ```
- **Response**: UserProfile object
- **Security**: Authenticated (JWT required)

#### 8. Upload Avatar
- **Method**: POST
- **Path**: `/api/profile/avatar`
- **Description**: Uploads a new avatar image
- **Request**: multipart/form-data with "avatar" field containing the image file
- **Response**: UserProfile object
- **Security**: Authenticated (JWT required)

#### 9. Delete Avatar
- **Method**: DELETE
- **Path**: `/api/profile/avatar`
- **Description**: Removes the user's avatar
- **Response**: UserProfile object
- **Security**: Authenticated (JWT required)

#### 10. Change Password
- **Method**: POST
- **Path**: `/api/profile/change-password`
- **Description**: Changes the user's password
- **Request Body**:
  ```json
  {
    "currentPassword": "string",
    "newPassword": "string"
  }
  ```
- **Response**:
  ```json
  {
    "message": "string"
  }
  ```
- **Security**: Authenticated (JWT required)

## Data Models

### User Entity
```java
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;
    
    private String avatar;
    private String firstName;
    private String lastName;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
    @Embedded
    private UserPreferences preferences;
    
    // Getters and setters
}
```

### Role Entity
```java
@Entity
@Table(name = "roles")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    private String description;
    
    // Getters and setters
}
```

### DTOs

#### LoginRequest
```java
public class LoginRequest {
    @NotNull
    @Email
    private String email;
    
    @NotNull
    @Size(min = 8)
    private String password;
}
```

#### RegisterRequest
```java
public class RegisterRequest {
    @NotNull
    @Email
    private String email;
    
    @NotNull
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$")
    private String username;
    
    @NotNull
    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")
    private String password;
    
    @NotNull
    private String confirmPassword;
}
```

#### UpdateProfileRequest
```java
public class UpdateProfileRequest {
    @NotNull
    @Size(min = 3, max = 50)
    private String username;
    
    @NotNull
    @Email
    private String email;
    
    @Size(max = 50)
    private String firstName;
    
    @Size(max = 50)
    private String lastName;
}
```

#### ChangePasswordRequest
```java
public class ChangePasswordRequest {
    @NotNull
    private String currentPassword;
    
    @NotNull
    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")
    private String newPassword;
}
```

## Security Configuration

### JWT Configuration
- **Access Token Expiration**: 15 minutes
- **Refresh Token Expiration**: 7 days
- **Secret Key**: Configured in application.properties

### Protected Paths
- `/api/profile/**` - Requires authentication
- `/api/user/**` - Requires authentication

### Public Paths
- `/api/auth/login`
- `/api/auth/register`
- `/api/auth/refresh`
- `/api/auth/forgot-password`
- `/api/auth/reset-password`

### User Roles
- `ROLE_USER` - Default role for registered users
- `ROLE_ADMIN` - Administrative privileges

## Validation Rules

### Email
- Must be a valid email format
- Required field

### Username
- Minimum length: 3 characters
- Maximum length: 50 characters
- Pattern: Alphanumeric with underscores only
- Required field

### Password
- Minimum length: 8 characters
- Must contain at least one uppercase letter
- Must contain at least one lowercase letter
- Must contain at least one digit
- Required field

### First Name / Last Name
- Maximum length: 50 characters
- Optional fields

### Avatar
- Maximum file size: 5MB
- Allowed formats: JPG, JPEG, PNG, GIF
- Optional field

## Implementation Notes

1. **Password Encoding**: Use BCryptPasswordEncoder for password hashing
2. **JWT Implementation**: Use io.jsonwebtoken library for token generation and validation
3. **File Storage**: Avatar images should be stored in a configurable directory with unique filenames
4. **Error Handling**: All endpoints should return appropriate HTTP status codes and error messages
5. **Logging**: Implement comprehensive logging for security events (login attempts, password changes, etc.)
6. **Rate Limiting**: Consider implementing rate limiting for authentication endpoints
7. **Email Service**: Implement email service for password reset functionality
8. **Validation**: Use Bean Validation annotations for request validation
9. **Transaction Management**: Ensure proper transaction boundaries for database operations
10. **Caching**: Consider caching user profiles to reduce database load