package com.taskboard.api.service;

import com.taskboard.api.config.StorageConfig;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.http.Method;
import java.io.InputStream;
import java.time.Duration;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/** Сервис для работы с облачным хранилищем (S3/MinIO). Поддерживает как MinIO, так и AWS S3. */
@Service
@Slf4j
@SuppressWarnings("checkstyle:DesignForExtension")
public class StorageService {

  private static final String MINIO_PROVIDER = "minio";

  private final StorageConfig storageConfig;
  private final MinioClient minioClient;
  private final S3Client s3Client;
  private final LocalStorageService localStorageService;
  private boolean useLocalStorage = false;

  public StorageService(
      final StorageConfig storageConfig,
      final MinioClient minioClient,
      final S3Client s3Client,
      final LocalStorageService localStorageService) {
    this.storageConfig = storageConfig;
    this.minioClient = minioClient;
    this.s3Client = s3Client;
    this.localStorageService = localStorageService;

    // Инициализируем локальное хранилище
    this.localStorageService.initializeStorage();

    // Проверяем доступность MinIO
    this.checkMinioAvailability();
  }

  /** Генерирует presigned URL для загрузки файла */
  public String generatePresignedUploadUrl(final String storageKey, final Duration expiration) {
    try {
      if (useLocalStorage) {
        log.info("Using local storage for upload: {}", storageKey);
        return localStorageService.generatePresignedUploadUrl(storageKey, expiration);
      }

      if (MINIO_PROVIDER.equals(storageConfig.getProvider())) {
        return generateMinioPresignedUploadUrl(storageKey, expiration);
      } else {
        return generateS3PresignedUploadUrl(storageKey, expiration);
      }
    } catch (Exception e) {
      log.error("Error generating presigned upload URL for key: {}", storageKey, e);
      // Fallback to local storage
      log.info("Falling back to local storage for key: {}", storageKey);
      this.useLocalStorage = true;
      return localStorageService.generatePresignedUploadUrl(storageKey, expiration);
    }
  }

  /** Генерирует presigned URL для скачивания файла */
  public String generatePresignedDownloadUrl(final String storageKey, final Duration expiration) {
    try {
      if (useLocalStorage) {
        return localStorageService.generatePresignedDownloadUrl(storageKey, expiration);
      }

      if (MINIO_PROVIDER.equals(storageConfig.getProvider())) {
        return generateMinioPresignedDownloadUrl(storageKey, expiration);
      } else {
        return generateS3PresignedDownloadUrl(storageKey, expiration);
      }
    } catch (Exception e) {
      log.error("Error generating presigned download URL for key: {}", storageKey, e);
      // Fallback to local storage
      this.useLocalStorage = true;
      return localStorageService.generatePresignedDownloadUrl(storageKey, expiration);
    }
  }

  /** Загружает файл напрямую в хранилище */
  public void uploadFile(
      final String storageKey, final InputStream inputStream, final String contentType, final long fileSize) {
    try {
      if (useLocalStorage) {
        localStorageService.uploadFile(storageKey, inputStream, contentType, fileSize);
      } else if (MINIO_PROVIDER.equals(storageConfig.getProvider())) {
        uploadToMinio(storageKey, inputStream, contentType, fileSize);
      } else {
        uploadToS3(storageKey, inputStream, contentType, fileSize);
      }
      log.info("File uploaded successfully: {}", storageKey);
    } catch (Exception e) {
      log.error("Error uploading file: {}", storageKey, e);
      // Fallback to local storage
      this.useLocalStorage = true;
      localStorageService.uploadFile(storageKey, inputStream, contentType, fileSize);
    }
  }

  /** Удаляет файл из хранилища */
  public void deleteFile(final String storageKey) {
    try {
      if (useLocalStorage) {
        localStorageService.deleteFile(storageKey);
      } else if (MINIO_PROVIDER.equals(storageConfig.getProvider())) {
        deleteFromMinio(storageKey);
      } else {
        deleteFromS3(storageKey);
      }
      log.info("File deleted successfully: {}", storageKey);
    } catch (Exception e) {
      log.error("Error deleting file: {}", storageKey, e);
      // Fallback to local storage
      this.useLocalStorage = true;
      localStorageService.deleteFile(storageKey);
    }
  }

  /** Проверяет существование файла */
  public boolean fileExists(final String storageKey) {
    try {
      if (useLocalStorage) {
        return localStorageService.fileExists(storageKey);
      }

      if (MINIO_PROVIDER.equals(storageConfig.getProvider())) {
        return fileExistsInMinio(storageKey);
      } else {
        return fileExistsInS3(storageKey);
      }
    } catch (Exception e) {
      log.error("Error checking file existence: {}", storageKey, e);
      // Fallback to local storage
      this.useLocalStorage = true;
      return localStorageService.fileExists(storageKey);
    }
  }

  /** Получает метаданные файла */
  public Map<String, String> getFileMetadata(final String storageKey) {
    try {
      if (useLocalStorage) {
        return localStorageService.getFileMetadata(storageKey);
      }

      if (MINIO_PROVIDER.equals(storageConfig.getProvider())) {
        return getMinioFileMetadata(storageKey);
      } else {
        return getS3FileMetadata(storageKey);
      }
    } catch (Exception e) {
      log.error("Error getting file metadata: {}", storageKey, e);
      // Fallback to local storage
      this.useLocalStorage = true;
      return localStorageService.getFileMetadata(storageKey);
    }
  }

  // MinIO методы
  private String generateMinioPresignedUploadUrl(final String storageKey, final Duration expiration)
      throws Exception {
    return minioClient.getPresignedObjectUrl(
        GetPresignedObjectUrlArgs.builder()
            .method(Method.PUT)
            .bucket(storageConfig.getBucketName())
            .object(storageKey)
            .expiry((int) expiration.toSeconds())
            .build());
  }

  private String generateMinioPresignedDownloadUrl(final String storageKey, final Duration expiration)
      throws Exception {
    return minioClient.getPresignedObjectUrl(
        GetPresignedObjectUrlArgs.builder()
            .method(Method.GET)
            .bucket(storageConfig.getBucketName())
            .object(storageKey)
            .expiry((int) expiration.toSeconds())
            .build());
  }

  private void uploadToMinio(
      final String storageKey, final InputStream inputStream, final String contentType, final long fileSize)
      throws Exception {
    minioClient.putObject(
        PutObjectArgs.builder().bucket(storageConfig.getBucketName()).object(storageKey).stream(
                inputStream, fileSize, -1)
            .contentType(contentType)
            .build());
  }

  private void deleteFromMinio(final String storageKey) throws Exception {
    minioClient.removeObject(
        RemoveObjectArgs.builder()
            .bucket(storageConfig.getBucketName())
            .object(storageKey)
            .build());
  }

  private boolean fileExistsInMinio(final String storageKey) throws Exception {
    try {
      minioClient.statObject(
          StatObjectArgs.builder()
              .bucket(storageConfig.getBucketName())
              .object(storageKey)
              .build());
      return true;
    } catch (Exception e) {
      // Handle MinIO errors
      if (e.getMessage() != null && e.getMessage().contains("NoSuchKey")) {
        return false;
      }
      throw new RuntimeException("Error checking file existence: " + e.getMessage(), e);
    }
  }

  private Map<String, String> getMinioFileMetadata(final String storageKey) throws Exception {
    StatObjectResponse stat =
        minioClient.statObject(
            StatObjectArgs.builder()
                .bucket(storageConfig.getBucketName())
                .object(storageKey)
                .build());

    return Map.of(
        "contentType", stat.contentType(),
        "size", String.valueOf(stat.size()),
        "lastModified", stat.lastModified().toString());
  }

  // AWS S3 методы - temporarily disabled due to SDK compatibility issues
  private String generateS3PresignedUploadUrl(final String storageKey, final Duration expiration) {
    // TODO(@developer): Implement S3 presigned URL generation when SDK is compatible
    throw new UnsupportedOperationException("S3 presigned URLs not yet implemented");
  }

  private String generateS3PresignedDownloadUrl(final String storageKey, final Duration expiration) {
    // TODO(@developer): Implement S3 presigned URL generation when SDK is compatible
    throw new UnsupportedOperationException("S3 presigned URLs not yet implemented");
  }

  private void uploadToS3(
      final String storageKey, final InputStream inputStream, final String contentType, final long fileSize) {
    if (s3Client == null) {
      throw new UnsupportedOperationException("S3Client is not available in development mode");
    }

    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder()
            .bucket(storageConfig.getBucketName())
            .key(storageKey)
            .contentType(contentType)
            .build();

    s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, fileSize));
  }

  private void deleteFromS3(final String storageKey) {
    if (s3Client == null) {
      throw new UnsupportedOperationException("S3Client is not available in development mode");
    }

    DeleteObjectRequest deleteObjectRequest =
        DeleteObjectRequest.builder().bucket(storageConfig.getBucketName()).key(storageKey).build();

    s3Client.deleteObject(deleteObjectRequest);
  }

  private boolean fileExistsInS3(final String storageKey) {
    if (s3Client == null) {
      throw new UnsupportedOperationException("S3Client is not available in development mode");
    }

    try {
      HeadObjectRequest headObjectRequest =
          HeadObjectRequest.builder().bucket(storageConfig.getBucketName()).key(storageKey).build();

      s3Client.headObject(headObjectRequest);
      return true;
    } catch (NoSuchKeyException e) {
      return false;
    }
  }

  private Map<String, String> getS3FileMetadata(final String storageKey) {
    if (s3Client == null) {
      throw new UnsupportedOperationException("S3Client is not available in development mode");
    }

    HeadObjectRequest headObjectRequest =
        HeadObjectRequest.builder().bucket(storageConfig.getBucketName()).key(storageKey).build();

    HeadObjectResponse response = s3Client.headObject(headObjectRequest);

    return Map.of(
        "contentType", response.contentType(),
        "size", String.valueOf(response.contentLength()),
        "lastModified", response.lastModified().toString());
  }

  /** Проверяет доступность MinIO */
  private void checkMinioAvailability() {
    try {
      if (MINIO_PROVIDER.equals(storageConfig.getProvider())) {
        // Пытаемся выполнить простую операцию с MinIO
        minioClient.listBuckets();
        log.info("MinIO is available, using cloud storage");
        this.useLocalStorage = false;
      } else {
        log.info("Using S3 storage");
        this.useLocalStorage = false;
      }
    } catch (Exception e) {
      log.warn("MinIO/S3 is not available, falling back to local storage: {}", e.getMessage());
      this.useLocalStorage = true;
    }
  }
}
