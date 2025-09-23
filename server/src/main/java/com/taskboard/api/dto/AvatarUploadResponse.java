package com.taskboard.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для ответа с presigned URL для загрузки аватара.
 */
@Data
@Builder
public class AvatarUploadResponse {

    private String uploadUrl;
    private String storageKey;
    private LocalDateTime expiresAt;
    private String fields; // Дополнительные поля для multipart upload (если нужны)
}
