package com.taskboard.api.service;

import com.taskboard.api.config.StorageConfig;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Сервис для работы с облачным хранилищем (S3/MinIO).
 * Поддерживает как MinIO, так и AWS S3.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final StorageConfig storageConfig;
    private final MinioClient minioClient;
    private final S3Client s3Client;

    /**
     * Генерирует presigned URL для загрузки файла
     */
    public String generatePresignedUploadUrl(String storageKey, Duration expiration) {
        try {
            if ("minio".equals(storageConfig.getProvider())) {
                return generateMinioPresignedUploadUrl(storageKey, expiration);
            } else {
                return generateS3PresignedUploadUrl(storageKey, expiration);
            }
        } catch (Exception e) {
            log.error("Error generating presigned upload URL for key: {}", storageKey, e);
            throw new RuntimeException("Failed to generate upload URL", e);
        }
    }

    /**
     * Генерирует presigned URL для скачивания файла
     */
    public String generatePresignedDownloadUrl(String storageKey, Duration expiration) {
        try {
            if ("minio".equals(storageConfig.getProvider())) {
                return generateMinioPresignedDownloadUrl(storageKey, expiration);
            } else {
                return generateS3PresignedDownloadUrl(storageKey, expiration);
            }
        } catch (Exception e) {
            log.error("Error generating presigned download URL for key: {}", storageKey, e);
            throw new RuntimeException("Failed to generate download URL", e);
        }
    }

    /**
     * Загружает файл напрямую в хранилище
     */
    public void uploadFile(String storageKey, InputStream inputStream, String contentType, long fileSize) {
        try {
            if ("minio".equals(storageConfig.getProvider())) {
                uploadToMinio(storageKey, inputStream, contentType, fileSize);
            } else {
                uploadToS3(storageKey, inputStream, contentType, fileSize);
            }
            log.info("File uploaded successfully: {}", storageKey);
        } catch (Exception e) {
            log.error("Error uploading file: {}", storageKey, e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    /**
     * Удаляет файл из хранилища
     */
    public void deleteFile(String storageKey) {
        try {
            if ("minio".equals(storageConfig.getProvider())) {
                deleteFromMinio(storageKey);
            } else {
                deleteFromS3(storageKey);
            }
            log.info("File deleted successfully: {}", storageKey);
        } catch (Exception e) {
            log.error("Error deleting file: {}", storageKey, e);
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    /**
     * Проверяет существование файла
     */
    public boolean fileExists(String storageKey) {
        try {
            if ("minio".equals(storageConfig.getProvider())) {
                return fileExistsInMinio(storageKey);
            } else {
                return fileExistsInS3(storageKey);
            }
        } catch (Exception e) {
            log.error("Error checking file existence: {}", storageKey, e);
            return false;
        }
    }

    /**
     * Получает метаданные файла
     */
    public Map<String, String> getFileMetadata(String storageKey) {
        try {
            if ("minio".equals(storageConfig.getProvider())) {
                return getMinioFileMetadata(storageKey);
            } else {
                return getS3FileMetadata(storageKey);
            }
        } catch (Exception e) {
            log.error("Error getting file metadata: {}", storageKey, e);
            throw new RuntimeException("Failed to get file metadata", e);
        }
    }

    // MinIO методы
    private String generateMinioPresignedUploadUrl(String storageKey, Duration expiration) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket(storageConfig.getBucketName())
                        .object(storageKey)
                        .expiry((int) expiration.toSeconds())
                        .build()
        );
    }

    private String generateMinioPresignedDownloadUrl(String storageKey, Duration expiration) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(storageConfig.getBucketName())
                        .object(storageKey)
                        .expiry((int) expiration.toSeconds())
                        .build()
        );
    }

    private void uploadToMinio(String storageKey, InputStream inputStream, String contentType, long fileSize) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(storageConfig.getBucketName())
                        .object(storageKey)
                        .stream(inputStream, fileSize, -1)
                        .contentType(contentType)
                        .build()
        );
    }

    private void deleteFromMinio(String storageKey) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(storageConfig.getBucketName())
                        .object(storageKey)
                        .build()
        );
    }

    private boolean fileExistsInMinio(String storageKey) throws Exception {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(storageConfig.getBucketName())
                            .object(storageKey)
                            .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return false;
            }
            throw e;
        }
    }

    private Map<String, String> getMinioFileMetadata(String storageKey) throws Exception {
        StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(storageConfig.getBucketName())
                        .object(storageKey)
                        .build()
        );

        return Map.of(
                "contentType", stat.contentType(),
                "size", String.valueOf(stat.size()),
                "lastModified", stat.lastModified().toString()
        );
    }

    // AWS S3 методы
    private String generateS3PresignedUploadUrl(String storageKey, Duration expiration) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(storageConfig.getBucketName())
                .key(storageKey)
                .build();

        return s3Client.utilities().getPresignedUrl(builder -> builder
                .putObjectRequest(putObjectRequest)
                .signatureDuration(expiration)
                .build()).toString();
    }

    private String generateS3PresignedDownloadUrl(String storageKey, Duration expiration) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(storageConfig.getBucketName())
                .key(storageKey)
                .build();

        return s3Client.utilities().getPresignedUrl(builder -> builder
                .getObjectRequest(getObjectRequest)
                .signatureDuration(expiration)
                .build()).toString();
    }

    private void uploadToS3(String storageKey, InputStream inputStream, String contentType, long fileSize) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(storageConfig.getBucketName())
                .key(storageKey)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, fileSize));
    }

    private void deleteFromS3(String storageKey) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(storageConfig.getBucketName())
                .key(storageKey)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    private boolean fileExistsInS3(String storageKey) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(storageConfig.getBucketName())
                    .key(storageKey)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    private Map<String, String> getS3FileMetadata(String storageKey) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(storageConfig.getBucketName())
                .key(storageKey)
                .build();

        HeadObjectResponse response = s3Client.headObject(headObjectRequest);

        return Map.of(
                "contentType", response.contentType(),
                "size", String.valueOf(response.contentLength()),
                "lastModified", response.lastModified().toString()
        );
    }
}
