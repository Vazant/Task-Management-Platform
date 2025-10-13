package com.taskboard.api.constants;

/**
 * Константы для работы с хранилищем файлов.
 */
public final class StorageConstants {

    private StorageConstants() {
        // Utility class
    }

    // Storage providers
    public static final String STORAGE_PROVIDER_MINIO = "minio";
    public static final String STORAGE_PROVIDER_S3 = "s3";

    // Default values
    public static final String DEFAULT_REGION = "us-east-1";
    public static final boolean DEFAULT_USE_HTTPS = true;
    public static final int DEFAULT_CONNECTION_TIMEOUT = 30000;
    public static final int DEFAULT_READ_TIMEOUT = 60000;

    // File constraints
    public static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024; // 10 MB
    public static final String[] ALLOWED_IMAGE_TYPES = {
        "image/jpeg", "image/png", "image/gif", "image/webp"
    };

    // Paths
    public static final String AVATAR_UPLOAD_DIR = "uploads/avatars/";
    public static final String PROJECT_FILES_DIR = "uploads/projects/";

    // Error messages
    public static final String ERROR_STORAGE_ENDPOINT_REQUIRED = "Storage endpoint is required";
    public static final String ERROR_STORAGE_ACCESS_KEY_REQUIRED = "Storage access key is required";
    public static final String ERROR_STORAGE_SECRET_KEY_REQUIRED = "Storage secret key is required";
    public static final String ERROR_STORAGE_BUCKET_REQUIRED = "Storage bucket name is required";
    public static final String ERROR_FILE_UPLOAD_FAILED = "Failed to upload file";
    public static final String ERROR_FILE_DELETE_FAILED = "Failed to delete file";
    public static final String ERROR_FILE_SIZE_EXCEEDED = "File size exceeds maximum allowed size";
    public static final String ERROR_FILE_TYPE_NOT_ALLOWED = "File type not allowed";
}


















