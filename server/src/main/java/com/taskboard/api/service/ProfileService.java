package com.taskboard.api.service;

import com.taskboard.api.dto.ProfileResponse;
import com.taskboard.api.dto.UpdateProfileRequest;
import com.taskboard.api.dto.ChangePasswordRequest;
import com.taskboard.api.model.User;
import com.taskboard.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class ProfileService {

    private static final int MAX_FILE_SIZE_MB = 5;
    private static final int BYTES_PER_MB = 1024 * 1024;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String AVATAR_UPLOAD_DIR = "uploads/avatars/";

    /**
     * Получить профиль пользователя.
     */
    public ProfileResponse getProfile(final String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return convertToProfileResponse(user);
    }

    /**
     * Обновить профиль пользователя.
     */
    public ProfileResponse updateProfile(final String username, final UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Проверяем, не занят ли username другим пользователем
        if (!username.equals(request.getUsername())) {
            userRepository.findByUsername(request.getUsername())
                .ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(user.getId())) {
                        throw new RuntimeException("Пользователь с таким именем уже существует");
                    }
                });
        }

        // Проверяем, не занят ли email другим пользователем
        if (!user.getEmail().equals(request.getEmail())) {
            userRepository.findByEmail(request.getEmail())
                .ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(user.getId())) {
                        throw new RuntimeException("Пользователь с таким email уже существует");
                    }
                });
        }

        // Обновляем данные
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return convertToProfileResponse(savedUser);
    }

    /**
     * Обновить аватар пользователя.
     * @deprecated Используйте новый AvatarService с presigned URL
     */
    @Deprecated
    public ProfileResponse updateAvatar(final String username, final MultipartFile file) {
        throw new UnsupportedOperationException(
            "Прямая загрузка файлов больше не поддерживается. " +
            "Используйте новый AvatarService с presigned URL: " +
            "POST /api/avatars/upload-url для получения URL загрузки, " +
            "затем POST /api/avatars/confirm для подтверждения загрузки."
        );
    }

    /**
     * Удалить аватар пользователя.
     */
    public ProfileResponse deleteAvatar(final String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Удаляем файл аватара, если он есть
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            try {
                Path avatarPath = Paths.get(user.getAvatar());
                if (Files.exists(avatarPath)) {
                    Files.delete(avatarPath);
                }
            } catch (IOException e) {
                // Логируем ошибку, но не прерываем выполнение
                System.err.println("Ошибка при удалении файла аватара: " + e.getMessage());
            }
        }

        // Очищаем путь к аватару в базе данных
        user.setAvatar(null);
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return convertToProfileResponse(savedUser);
    }

    /**
     * Изменить пароль пользователя.
     */
    public void changePassword(final String username, final ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Проверяем текущий пароль
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Неверный текущий пароль");
        }

        // Обновляем пароль
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    /**
     * Конвертирует User в ProfileResponse
     */
    private ProfileResponse convertToProfileResponse(final User user) {
        return new ProfileResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getAvatar(),
            user.getRole().name(),
            user.getLastLogin(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
