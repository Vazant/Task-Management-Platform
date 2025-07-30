package com.taskboard.api.controller;

import com.taskboard.api.dto.ApiResponse;
import com.taskboard.api.dto.AvatarConfirmRequest;
import com.taskboard.api.dto.AvatarResponse;
import com.taskboard.api.dto.AvatarUploadRequest;
import com.taskboard.api.dto.AvatarUploadResponse;
import com.taskboard.api.model.User;
import com.taskboard.api.repository.UserRepository;
import com.taskboard.api.service.AvatarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Контроллер для работы с аватарами пользователей.
 * Поддерживает presigned URL для загрузки и CDN для отдачи.
 */
@RestController
@RequestMapping("/api/avatars")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AvatarController {

    private final AvatarService avatarService;
    private final UserRepository userRepository;

    /**
     * Генерирует presigned URL для загрузки аватара
     */
    @PostMapping("/upload-url")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AvatarUploadResponse>> generateUploadUrl(
            @Valid @RequestBody AvatarUploadRequest request,
            Authentication authentication) {

        try {
            log.info("Upload URL request from user: {}", authentication.getName());

            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            AvatarUploadResponse response = avatarService.generateUploadUrl(user.getId(), request);

            return ResponseEntity.ok(new ApiResponse<>(response, "Upload URL generated successfully", true));

        } catch (Exception e) {
            log.error("Error generating upload URL for user: {}", authentication.getName(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error generating upload URL: " + e.getMessage(), false));
        }
    }

    /**
     * Подтверждает загрузку аватара
     */
    @PostMapping("/confirm")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AvatarResponse>> confirmUpload(
            @Valid @RequestBody AvatarConfirmRequest request,
            Authentication authentication) {

        try {
            log.info("Avatar upload confirmation from user: {}", authentication.getName());

            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            AvatarResponse response = avatarService.confirmUpload(user.getId(), request);

            return ResponseEntity.ok(new ApiResponse<>(response, "Avatar upload confirmed successfully", true));

        } catch (Exception e) {
            log.error("Error confirming avatar upload for user: {}", authentication.getName(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error confirming upload: " + e.getMessage(), false));
        }
    }

    /**
     * Получает активный аватар пользователя
     */
    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AvatarResponse>> getActiveAvatar(@PathVariable Long userId) {
        try {
            log.debug("Getting active avatar for user ID: {}", userId);

            AvatarResponse avatar = avatarService.getActiveAvatar(userId);

            if (avatar == null) {
                return ResponseEntity.ok(new ApiResponse<>(null, "No active avatar found", true));
            }

            return ResponseEntity.ok(new ApiResponse<>(avatar, "Active avatar retrieved successfully", true));

        } catch (Exception e) {
            log.error("Error getting active avatar for user ID: {}", userId, e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error retrieving avatar: " + e.getMessage(), false));
        }
    }

    /**
     * Получает все аватары пользователя
     */
    @GetMapping("/{userId}/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<AvatarResponse>>> getAllAvatars(@PathVariable Long userId) {
        try {
            log.debug("Getting all avatars for user ID: {}", userId);

            List<AvatarResponse> avatars = avatarService.getAllAvatars(userId);

            return ResponseEntity.ok(new ApiResponse<>(avatars, "All avatars retrieved successfully", true));

        } catch (Exception e) {
            log.error("Error getting all avatars for user ID: {}", userId, e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error retrieving avatars: " + e.getMessage(), false));
        }
    }

    /**
     * Удаляет аватар пользователя
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> deleteAvatar(
            @PathVariable Long userId,
            Authentication authentication) {

        try {
            log.info("Avatar deletion request from user: {} for user ID: {}", authentication.getName(), userId);

            // Проверяем права доступа (пользователь может удалять только свой аватар)
            User currentUser = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!currentUser.getId().equals(userId)) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse<>(null, "Access denied", false));
            }

            avatarService.deleteAvatar(userId);

            return ResponseEntity.ok(new ApiResponse<>("Avatar deleted", "Avatar deleted successfully", true));

        } catch (Exception e) {
            log.error("Error deleting avatar for user ID: {}", userId, e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error deleting avatar: " + e.getMessage(), false));
        }
    }

    /**
     * Генерирует presigned URL для скачивания аватара
     */
    @GetMapping("/{userId}/download-url")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> generateDownloadUrl(@PathVariable Long userId) {
        try {
            log.debug("Download URL request for user ID: {}", userId);

            String downloadUrl = avatarService.generateDownloadUrl(userId);

            return ResponseEntity.ok(new ApiResponse<>(downloadUrl, "Download URL generated successfully", true));

        } catch (Exception e) {
            log.error("Error generating download URL for user ID: {}", userId, e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error generating download URL: " + e.getMessage(), false));
        }
    }

    /**
     * Получает статистику по аватарам пользователя
     */
    @GetMapping("/{userId}/stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AvatarService.AvatarStats>> getAvatarStats(@PathVariable Long userId) {
        try {
            log.debug("Avatar stats request for user ID: {}", userId);

            AvatarService.AvatarStats stats = avatarService.getAvatarStats(userId);

            return ResponseEntity.ok(new ApiResponse<>(stats, "Avatar stats retrieved successfully", true));

        } catch (Exception e) {
            log.error("Error getting avatar stats for user ID: {}", userId, e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error retrieving avatar stats: " + e.getMessage(), false));
        }
    }

    /**
     * Получает активный аватар текущего пользователя
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AvatarResponse>> getMyActiveAvatar(Authentication authentication) {
        try {
            log.debug("Getting active avatar for current user: {}", authentication.getName());

            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            AvatarResponse avatar = avatarService.getActiveAvatar(user.getId());

            if (avatar == null) {
                return ResponseEntity.ok(new ApiResponse<>(null, "No active avatar found", true));
            }

            return ResponseEntity.ok(new ApiResponse<>(avatar, "Active avatar retrieved successfully", true));

        } catch (Exception e) {
            log.error("Error getting active avatar for user: {}", authentication.getName(), e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, "Error retrieving avatar: " + e.getMessage(), false));
        }
    }
}
