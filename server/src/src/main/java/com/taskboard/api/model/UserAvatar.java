package com.taskboard.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

/**
 * Entity для хранения метаданных аватаров пользователей.
 * Сами файлы хранятся в S3/MinIO, здесь только метаданные.
 */
@Entity
@Table(name = "user_avatars")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "user")
@SQLDelete(sql = "UPDATE user_avatars SET deleted_at = CURRENT_TIMESTAMP, is_active = false WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class UserAvatar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Storage key is required")
    @Column(name = "storage_key", nullable = false, length = 500)
    private String storageKey;

    @Positive(message = "Version must be positive")
    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @NotNull(message = "Content type is required")
    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Positive(message = "File size must be positive")
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "original_filename", length = 255)
    private String originalFilename;

    @Column(name = "cdn_url", length = 1000)
    private String cdnUrl;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Связь с пользователем (только для чтения)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    /**
     * Конструктор для создания нового аватара
     */
    public UserAvatar(Long userId, String storageKey, String contentType, Long fileSize, String originalFilename) {
        this.userId = userId;
        this.storageKey = storageKey;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.originalFilename = originalFilename;
        this.uploadedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
    }

    /**
     * Генерирует полный URL для доступа к аватару
     */
    public String getFullUrl() {
        if (cdnUrl != null && !cdnUrl.isEmpty()) {
            return cdnUrl;
        }
        // Fallback на прямой URL к S3/MinIO
        return storageKey;
    }

    /**
     * Проверяет, является ли аватар изображением
     */
    public boolean isImage() {
        return contentType != null && contentType.startsWith("image/");
    }

    /**
     * Получает расширение файла из оригинального имени
     */
    public String getFileExtension() {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return "";
        }
        int lastDotIndex = originalFilename.lastIndexOf('.');
        return lastDotIndex > 0 ? originalFilename.substring(lastDotIndex) : "";
    }

    /**
     * Получает размер файла в мегабайтах
     */
    public double getFileSizeInMB() {
        return fileSize != null ? fileSize / (1024.0 * 1024.0) : 0.0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
