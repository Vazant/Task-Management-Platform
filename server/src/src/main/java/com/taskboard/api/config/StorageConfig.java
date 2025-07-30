package com.taskboard.api.config;

import io.minio.MinioClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

/**
 * Конфигурация для облачного хранилища (S3/MinIO).
 */
@Configuration
@ConfigurationProperties(prefix = "app.storage")
@Data
@Slf4j
public class StorageConfig {

    private String provider = "minio"; // "s3" или "minio"
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String region = "us-east-1";
    private String cdnBaseUrl;
    private boolean useHttps = true;
    private int connectionTimeout = 30000;
    private int readTimeout = 60000;

    /**
     * Конфигурация для MinIO (локальное/приватное облачное хранилище)
     */
    @Bean
    @Profile("!prod")
    public MinioClient minioClient() {
        log.info("Initializing MinIO client with endpoint: {}", endpoint);

        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    /**
     * Конфигурация для AWS S3 (продакшн)
     */
    @Bean
    @Profile("prod")
    public S3Client s3Client() {
        log.info("Initializing AWS S3 client for region: {}", region);

        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

    /**
     * Получить полный URL к объекту
     */
    public String getObjectUrl(String storageKey) {
        if (cdnBaseUrl != null && !cdnBaseUrl.isEmpty()) {
            return cdnBaseUrl + "/" + storageKey;
        }

        String protocol = useHttps ? "https" : "http";
        return protocol + "://" + endpoint + "/" + bucketName + "/" + storageKey;
    }

    /**
     * Проверить конфигурацию
     */
    public void validate() {
        if (endpoint == null || endpoint.isEmpty()) {
            throw new IllegalStateException("Storage endpoint is required");
        }
        if (accessKey == null || accessKey.isEmpty()) {
            throw new IllegalStateException("Storage access key is required");
        }
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("Storage secret key is required");
        }
        if (bucketName == null || bucketName.isEmpty()) {
            throw new IllegalStateException("Storage bucket name is required");
        }

        log.info("Storage configuration validated successfully");
        log.info("Provider: {}", provider);
        log.info("Endpoint: {}", endpoint);
        log.info("Bucket: {}", bucketName);
        log.info("CDN Base URL: {}", cdnBaseUrl != null ? cdnBaseUrl : "Not configured");
    }
}
