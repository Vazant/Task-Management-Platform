package com.taskboard.api.service;

import com.taskboard.api.config.StorageConfig;
import com.taskboard.api.constants.ErrorMessages;
import com.taskboard.api.dto.AvatarConfirmRequest;
import com.taskboard.api.dto.AvatarResponse;
import com.taskboard.api.dto.AvatarUploadRequest;
import com.taskboard.api.dto.AvatarUploadResponse;
import com.taskboard.api.repository.UserAvatarRepository;
import com.taskboard.user.model.UserAvatar;
import com.taskboard.user.model.UserEntity;
import com.taskboard.user.repository.UserRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Бизнес-сервис для работы с аватарами пользователей. */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AvatarService {

  private final UserRepository userRepository;
  private final UserAvatarRepository userAvatarRepository;
  private final StorageService storageService;
  private final StorageConfig storageConfig;

  private static final Duration UPLOAD_URL_EXPIRATION = Duration.ofMinutes(15);
  private static final Duration DOWNLOAD_URL_EXPIRATION = Duration.ofHours(1);
  private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

  /** Генерирует presigned URL для загрузки аватара */
  public AvatarUploadResponse generateUploadUrl(Long userId, AvatarUploadRequest request) {
    // Logging moved to LoggingUtils for consistency

    // Валидация пользователя
    UserEntity user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new RuntimeException(ErrorMessages.USER_NOT_FOUND + userId));

    // Валидация размера файла
    if (request.getFileSize() != null && request.getFileSize() > MAX_FILE_SIZE) {
      throw new RuntimeException(
          "File size exceeds maximum allowed size: " + MAX_FILE_SIZE + " bytes");
    }

    // Генерация storage key
    String storageKey = generateStorageKey(userId, request.getFileName());

    // Генерация presigned URL
    String uploadUrl = storageService.generatePresignedUploadUrl(storageKey, UPLOAD_URL_EXPIRATION);

    LocalDateTime expiresAt = LocalDateTime.now().plus(UPLOAD_URL_EXPIRATION);

    // Success logging removed for performance

    return AvatarUploadResponse.builder()
        .uploadUrl(uploadUrl)
        .storageKey(storageKey)
        .expiresAt(expiresAt)
        .build();
  }

  /** Подтверждает загрузку аватара и сохраняет метаданные в БД */
  public AvatarResponse confirmUpload(Long userId, AvatarConfirmRequest request) {
    log.info(
        "Confirming avatar upload for user: {}, storage key: {}", userId, request.getStorageKey());

    // Валидация пользователя
    UserEntity user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new RuntimeException(ErrorMessages.USER_NOT_FOUND + userId));

    // Проверка существования файла в хранилище
    if (!storageService.fileExists(request.getStorageKey())) {
      throw new RuntimeException("File not found in storage: " + request.getStorageKey());
    }

    // Получение метаданных файла
    var metadata = storageService.getFileMetadata(request.getStorageKey());
    String contentType = metadata.get("contentType");
    Long fileSize = Long.parseLong(metadata.get("size"));

    // Деактивация предыдущих аватаров пользователя
    userAvatarRepository.deactivateAllByUserId(userId);

    // Получение следующей версии
    Integer nextVersion = userAvatarRepository.getMaxVersionByUserId(userId) + 1;

    // Создание записи в БД
    UserAvatar avatar =
        new UserAvatar(
            userId,
            request.getStorageKey(),
            contentType,
            fileSize,
            extractOriginalFilename(request.getStorageKey()));
    avatar.setVersion(nextVersion);

    // Генерация CDN URL
    String cdnUrl = storageConfig.getObjectUrl(request.getStorageKey());
    avatar.setCdnUrl(cdnUrl);

    // Если используется локальное хранилище, генерируем локальный URL
    if (cdnUrl.startsWith("http://localhost:9000") || cdnUrl.contains("localhost")) {
      String localUrl = "/api/avatars/file/" + request.getStorageKey();
      avatar.setCdnUrl(localUrl);
    }

    UserAvatar savedAvatar = userAvatarRepository.save(avatar);

    log.info("Avatar upload confirmed for user: {}, avatar ID: {}", userId, savedAvatar.getId());

    return convertToAvatarResponse(savedAvatar);
  }

  /** Получает активный аватар пользователя */
  @Transactional(readOnly = true)
  public AvatarResponse getActiveAvatar(Long userId) {
    log.debug("Getting active avatar for user: {}", userId);

    UserAvatar avatar = userAvatarRepository.findByUserIdAndIsActiveTrue(userId).orElse(null);

    if (avatar == null) {
      log.debug("No active avatar found for user: {}", userId);
      return null;
    }

    return convertToAvatarResponse(avatar);
  }

  /** Получает все аватары пользователя */
  @Transactional(readOnly = true)
  public List<AvatarResponse> getAllAvatars(Long userId) {
    log.debug("Getting all avatars for user: {}", userId);

    List<UserAvatar> avatars = userAvatarRepository.findByUserIdOrderByVersionDesc(userId);

    return avatars.stream().map(this::convertToAvatarResponse).collect(Collectors.toList());
  }

  /** Удаляет аватар пользователя */
  public void deleteAvatar(Long userId) {
    log.info("Deleting avatar for user: {}", userId);

    UserAvatar avatar = userAvatarRepository.findByUserIdAndIsActiveTrue(userId).orElse(null);

    if (avatar != null) {
      // Удаление файла из хранилища
      storageService.deleteFile(avatar.getStorageKey());

      // Удаление записи из БД
      userAvatarRepository.delete(avatar);

      log.info("Avatar deleted successfully for user: {}", userId);
    } else {
      log.debug("No active avatar found for user: {}", userId);
    }
  }

  /** Генерирует presigned URL для скачивания аватара */
  @Transactional(readOnly = true)
  public String generateDownloadUrl(Long userId) {
    log.debug("Generating download URL for user: {}", userId);

    UserAvatar avatar =
        userAvatarRepository
            .findByUserIdAndIsActiveTrue(userId)
            .orElseThrow(() -> new RuntimeException("No active avatar found for user: " + userId));

    return storageService.generatePresignedDownloadUrl(
        avatar.getStorageKey(), DOWNLOAD_URL_EXPIRATION);
  }

  /** Получает статистику по аватарам пользователя */
  @Transactional(readOnly = true)
  public AvatarStats getAvatarStats(Long userId) {
    long totalAvatars = userAvatarRepository.countByUserId(userId);
    long totalSize = userAvatarRepository.getTotalSizeByUserId(userId);
    boolean hasActiveAvatar = userAvatarRepository.existsByUserIdAndIsActiveTrue(userId);

    return new AvatarStats(totalAvatars, totalSize, hasActiveAvatar);
  }

  // Приватные методы

  private String generateStorageKey(Long userId, String fileName) {
    String extension = extractFileExtension(fileName);
    String uuid = UUID.randomUUID().toString();
    return String.format("avatars/%d/%s%s", userId, uuid, extension);
  }

  private String extractFileExtension(String fileName) {
    int lastDotIndex = fileName.lastIndexOf('.');
    return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : ".jpg";
  }

  private String extractOriginalFilename(String storageKey) {
    int lastSlashIndex = storageKey.lastIndexOf('/');
    if (lastSlashIndex > 0) {
      String filename = storageKey.substring(lastSlashIndex + 1);
      // Убираем UUID из имени файла
      int firstDotIndex = filename.indexOf('.');
      if (firstDotIndex > 0) {
        return "avatar" + filename.substring(firstDotIndex);
      }
    }
    return "avatar.jpg";
  }

  private AvatarResponse convertToAvatarResponse(UserAvatar avatar) {
    return AvatarResponse.builder()
        .id(avatar.getId())
        .userId(avatar.getUserId())
        .storageKey(avatar.getStorageKey())
        .version(avatar.getVersion())
        .contentType(avatar.getContentType())
        .fileSize(avatar.getFileSize())
        .originalFilename(avatar.getOriginalFilename())
        .cdnUrl(avatar.getCdnUrl())
        .fullUrl(avatar.getFullUrl())
        .uploadedAt(avatar.getUploadedAt())
        .updatedAt(avatar.getUpdatedAt())
        .isActive(avatar.getIsActive())
        .build();
  }

  /** Статистика по аватарам пользователя */
  public static class AvatarStats {
    private final long totalAvatars;
    private final long totalSizeBytes;
    private final boolean hasActiveAvatar;

    public AvatarStats(long totalAvatars, long totalSizeBytes, boolean hasActiveAvatar) {
      this.totalAvatars = totalAvatars;
      this.totalSizeBytes = totalSizeBytes;
      this.hasActiveAvatar = hasActiveAvatar;
    }

    public long getTotalAvatars() {
      return totalAvatars;
    }

    public long getTotalSizeBytes() {
      return totalSizeBytes;
    }

    public double getTotalSizeMB() {
      return totalSizeBytes / (1024.0 * 1024.0);
    }

    public boolean hasActiveAvatar() {
      return hasActiveAvatar;
    }
  }
}
