package com.taskboard.user.controller;

import com.taskboard.api.dto.ChangePasswordRequest;
import com.taskboard.api.dto.LoginRequest;
import com.taskboard.api.dto.LoginResponse;
import com.taskboard.api.dto.RegisterRequest;
import com.taskboard.api.dto.UpdateProfileRequest;
import com.taskboard.user.dto.ForgotPasswordRequest;
import com.taskboard.user.dto.MessageResponse;
import com.taskboard.user.dto.RefreshTokenRequest;
import com.taskboard.user.dto.RefreshTokenResponse;
import com.taskboard.user.dto.ResetPasswordRequest;
import com.taskboard.user.dto.UserDto;
import com.taskboard.user.dto.UserProfileDto;
import com.taskboard.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

/**
 * REST controller for user-related operations.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    // Authentication Endpoints

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for email: {}", request.getEmail());
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request for email: {}", request.getEmail());
        LoginResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = userService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Password reset request for email: {}", request.getEmail());
        MessageResponse response = userService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        MessageResponse response = userService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    // Profile Management Endpoints

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> getCurrentUserProfile(Principal principal) {
        UserProfileDto profile = userService.getCurrentUserProfile(principal.getName());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        String userId = getUserIdFromAuthentication(authentication);
        UserProfileDto profile = userService.updateProfile(userId, request);
        return ResponseEntity.ok(profile);
    }

    @PostMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> uploadAvatar(
            Authentication authentication,
            @RequestParam("avatar") MultipartFile file) {
        log.info("Avatar upload request for user: {}", authentication.getName());
        // In a real implementation, you would handle file upload here
        // For now, we'll just use a placeholder URL
        String userId = getUserIdFromAuthentication(authentication);
        String avatarUrl = "/uploads/avatars/" + userId + "-avatar.jpg";
        UserProfileDto profile = userService.updateAvatar(userId, avatarUrl);
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping("/profile/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> deleteAvatar(Authentication authentication) {
        String userId = getUserIdFromAuthentication(authentication);
        UserProfileDto profile = userService.deleteAvatar(userId);
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/profile/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        String userId = getUserIdFromAuthentication(authentication);
        userService.changePassword(userId, request);
        return ResponseEntity.ok(MessageResponse.of("Password changed successfully"));
    }

    /**
     * Extract user ID from authentication object.
     * In a real implementation, this would get the actual user ID.
     */
    private String getUserIdFromAuthentication(Authentication authentication) {
        // This is a simplified implementation
        // In a real app, you would extract the user ID from the authentication principal
        UserDto user = userService.findByUsername(authentication.getName());
        return user.getId();
    }
}
