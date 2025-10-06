package com.taskboard.api.config;

import com.taskboard.api.constants.StorageConstants;
import io.minio.MinioClient;
import java.net.URI;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/** Конфигурация для облачного хранилища (S3/MinIO). */
@Configuration
@ConfigurationProperties(prefix = "app.storage")
@Getter
@Setter
@Slf4j
public class StorageConfig {

  private String provider = StorageConstants.STORAGE_PROVIDER_MINIO; // "s3" или "minio"
  private String endpoint;
  private String accessKey;
  private String secretKey;
  private String bucketName;
  private String region = StorageConstants.DEFAULT_REGION;
  private String cdnBaseUrl;
  private boolean useHttps = StorageConstants.DEFAULT_USE_HTTPS;
  private int connectionTimeout = StorageConstants.DEFAULT_CONNECTION_TIMEOUT;
  private int readTimeout = StorageConstants.DEFAULT_READ_TIMEOUT;

  /** Конфигурация для MinIO (локальное/приватное облачное хранилище) */
  @Bean
  @Profile("!prod")
  public MinioClient minioClient() {
    log.info("Initializing MinIO client with endpoint: {}", endpoint);

    return MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
  }

  /** Configuration for AWS S3 (production) */
  @Bean
  @Profile("prod")
  public S3Client s3ClientProd() {
    log.info("Initializing AWS S3 client for region: {}", region);

    AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .build();
  }

  /** S3Client for development mode (MinIO compatible) */
  @Bean
  @Profile("!prod")
  public S3Client s3Client() {
    log.info("Creating S3Client for development with MinIO endpoint: {}", endpoint);

    AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

    return S3Client.builder()
        .endpointOverride(URI.create(endpoint))
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .forcePathStyle(true) // Required for MinIO
        .build();
  }

  /** Получить полный URL к объекту */
  public String getObjectUrl(String storageKey) {
    if (cdnBaseUrl != null && !cdnBaseUrl.isEmpty()) {
      return cdnBaseUrl + "/" + storageKey;
    }

    String protocol = useHttps ? "https" : "http";
    return protocol + "://" + endpoint + "/" + bucketName + "/" + storageKey;
  }

  /** Проверить конфигурацию */
  public void validate() {
    if (endpoint == null || endpoint.isEmpty()) {
      throw new IllegalStateException(StorageConstants.ERROR_STORAGE_ENDPOINT_REQUIRED);
    }
    if (accessKey == null || accessKey.isEmpty()) {
      throw new IllegalStateException(StorageConstants.ERROR_STORAGE_ACCESS_KEY_REQUIRED);
    }
    if (secretKey == null || secretKey.isEmpty()) {
      throw new IllegalStateException(StorageConstants.ERROR_STORAGE_SECRET_KEY_REQUIRED);
    }
    if (bucketName == null || bucketName.isEmpty()) {
      throw new IllegalStateException(StorageConstants.ERROR_STORAGE_BUCKET_REQUIRED);
    }

    log.info("Storage configuration validated successfully");
    log.info("Provider: {}", provider);
    log.info("Endpoint: {}", endpoint);
    log.info("Bucket: {}", bucketName);
    log.info("CDN Base URL: {}", cdnBaseUrl != null ? cdnBaseUrl : "Not configured");
  }
}
