package com.taskboard.api.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Локальный сервис хранения файлов для разработки. Используется как fallback когда MinIO/S3
 * недоступны.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LocalStorageService {

  // StorageConfig не используется в локальном хранилище

  @Value("${app.storage.local-path:./uploads}")
  private String localStoragePath;

  /** Генерирует presigned URL для загрузки файла (локальная эмуляция) */
  public String generatePresignedUploadUrl(String storageKey, Duration expiration) {
    log.info("Generating local upload URL for key: {}", storageKey);

    // Для локального хранения возвращаем специальный URL
    return "local://upload/"
        + storageKey
        + "?expires="
        + (System.currentTimeMillis() + expiration.toMillis());
  }

  /** Генерирует presigned URL для скачивания файла (локальная эмуляция) */
  public String generatePresignedDownloadUrl(String storageKey, Duration expiration) {
    log.info("Generating local download URL for key: {}", storageKey);

    // Возвращаем URL для доступа к локальному файлу
    return "/api/avatars/file/" + storageKey;
  }

  /** Загружает файл в локальное хранилище */
  public void uploadFile(
      String storageKey, InputStream inputStream, String contentType, long fileSize) {
    try {
      Path filePath = getFilePath(storageKey);
      Files.createDirectories(filePath.getParent());

      try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
        inputStream.transferTo(fos);
      }

      log.info("File uploaded to local storage: {}", filePath);
    } catch (Exception e) {
      log.error("Error uploading file to local storage: {}", storageKey, e);
      throw new RuntimeException("Failed to upload file to local storage", e);
    }
  }

  /** Удаляет файл из локального хранилища */
  public void deleteFile(String storageKey) {
    try {
      Path filePath = getFilePath(storageKey);
      Files.deleteIfExists(filePath);
      log.info("File deleted from local storage: {}", filePath);
    } catch (Exception e) {
      log.error("Error deleting file from local storage: {}", storageKey, e);
      throw new RuntimeException("Failed to delete file from local storage", e);
    }
  }

  /** Проверяет существование файла в локальном хранилище */
  public boolean fileExists(String storageKey) {
    try {
      Path filePath = getFilePath(storageKey);
      return Files.exists(filePath);
    } catch (Exception e) {
      log.error("Error checking file existence in local storage: {}", storageKey, e);
      return false;
    }
  }

  /** Получает метаданные файла из локального хранилища */
  public Map<String, String> getFileMetadata(String storageKey) {
    try {
      Path filePath = getFilePath(storageKey);

      if (!Files.exists(filePath)) {
        throw new RuntimeException("File not found: " + storageKey);
      }

      Map<String, String> metadata = new HashMap<>();
      metadata.put("contentType", getContentType(storageKey));
      metadata.put("size", String.valueOf(Files.size(filePath)));
      metadata.put("lastModified", Files.getLastModifiedTime(filePath).toString());

      return metadata;
    } catch (Exception e) {
      log.error("Error getting file metadata from local storage: {}", storageKey, e);
      throw new RuntimeException("Failed to get file metadata from local storage", e);
    }
  }

  /** Получает файл как InputStream */
  public InputStream getFile(String storageKey) {
    try {
      Path filePath = getFilePath(storageKey);
      return Files.newInputStream(filePath);
    } catch (Exception e) {
      log.error("Error getting file from local storage: {}", storageKey, e);
      throw new RuntimeException("Failed to get file from local storage", e);
    }
  }

  /** Получает полный URL к файлу */
  public String getFileUrl(String storageKey) {
    return "/api/avatars/file/" + storageKey;
  }

  /** Создает директорию для хранения файлов */
  public void initializeStorage() {
    try {
      Path storageDir = Paths.get(localStoragePath);
      Files.createDirectories(storageDir);
      log.info("Local storage initialized at: {}", storageDir.toAbsolutePath());
    } catch (Exception e) {
      log.error("Error initializing local storage", e);
      throw new RuntimeException("Failed to initialize local storage", e);
    }
  }

  /** Очищает старые файлы (старше указанного времени) */
  public void cleanupOldFiles(Duration maxAge) {
    try {
      Path storageDir = Paths.get(localStoragePath);
      if (!Files.exists(storageDir)) {
        return;
      }

      long cutoffTime = System.currentTimeMillis() - maxAge.toMillis();

      Files.walk(storageDir)
          .filter(Files::isRegularFile)
          .filter(
              path -> {
                try {
                  return Files.getLastModifiedTime(path).toMillis() < cutoffTime;
                } catch (Exception e) {
                  return false;
                }
              })
          .forEach(
              path -> {
                try {
                  Files.delete(path);
                  log.debug("Deleted old file: {}", path);
                } catch (Exception e) {
                  log.warn("Failed to delete old file: {}", path, e);
                }
              });

    } catch (Exception e) {
      log.error("Error cleaning up old files", e);
    }
  }

  // Приватные методы

  private Path getFilePath(String storageKey) {
    return Paths.get(localStoragePath, storageKey);
  }

  private String getContentType(String storageKey) {
    String extension = getFileExtension(storageKey);
    switch (extension.toLowerCase()) {
      case ".jpg":
      case ".jpeg":
        return "image/jpeg";
      case ".png":
        return "image/png";
      case ".gif":
        return "image/gif";
      case ".webp":
        return "image/webp";
      case ".svg":
        return "image/svg+xml";
      default:
        return "application/octet-stream";
    }
  }

  private String getFileExtension(String fileName) {
    int lastDotIndex = fileName.lastIndexOf('.');
    return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
  }
}
