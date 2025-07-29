package com.taskboard.api.controller;

import com.taskboard.api.dto.ApiResponse;
import com.taskboard.api.dto.ProfileResponse;
import com.taskboard.api.dto.UpdateProfileRequest;
import com.taskboard.api.dto.ChangePasswordRequest;
import com.taskboard.api.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    /**
     * Получить профиль текущего пользователя
     */
    @GetMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(Authentication authentication) {
        try {
            ProfileResponse profile = profileService.getProfile(authentication.getName());
            return ResponseEntity.ok(new ApiResponse<>(profile, "Профиль успешно загружен", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(null, "Ошибка загрузки профиля: " + e.getMessage(), false));
        }
    }

    /**
     * Обновить профиль пользователя
     */
    @PutMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        try {
            ProfileResponse profile = profileService.updateProfile(authentication.getName(), request);
            return ResponseEntity.ok(new ApiResponse<>(profile, "Профиль успешно обновлен", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(null, "Ошибка обновления профиля: " + e.getMessage(), false));
        }
    }

    /**
     * Загрузить аватар пользователя
     */
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProfileResponse>> uploadAvatar(
            @RequestParam("avatar") MultipartFile file,
            Authentication authentication) {
        try {
            ProfileResponse profile = profileService.updateAvatar(authentication.getName(), file);
            return ResponseEntity.ok(new ApiResponse<>(profile, "Аватар успешно обновлен", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(null, "Ошибка обновления аватара: " + e.getMessage(), false));
        }
    }

    /**
     * Удалить аватар пользователя
     */
    @DeleteMapping("/avatar")
    public ResponseEntity<ApiResponse<ProfileResponse>> deleteAvatar(Authentication authentication) {
        try {
            ProfileResponse profile = profileService.deleteAvatar(authentication.getName());
            return ResponseEntity.ok(new ApiResponse<>(profile, "Аватар успешно удален", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(null, "Ошибка удаления аватара: " + e.getMessage(), false));
        }
    }

    /**
     * Изменить пароль пользователя
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        try {
            profileService.changePassword(authentication.getName(), request);
            return ResponseEntity.ok(new ApiResponse<>("Пароль изменен", "Пароль успешно изменен", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(null, "Ошибка смены пароля: " + e.getMessage(), false));
        }
    }
}
