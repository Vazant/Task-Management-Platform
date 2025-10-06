package com.taskboard.api.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/** DTO для ответа с presigned URL для загрузки аватара. */
@Data
@Builder
public class AvatarUploadResponse {

  private String uploadUrl;
  private String storageKey;
  private LocalDateTime expiresAt;
  private String fields; // Дополнительные поля для multipart upload (если нужны)
}
