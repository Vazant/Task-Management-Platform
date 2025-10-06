package com.taskboard.api.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/** DTO для ответа с информацией об аватаре пользователя. */
@Data
@Builder
public class AvatarResponse {

  private Long id;
  private Long userId;
  private String storageKey;
  private Integer version;
  private String contentType;
  private Long fileSize;
  private String originalFilename;
  private String cdnUrl;
  private String fullUrl;
  private LocalDateTime uploadedAt;
  private LocalDateTime updatedAt;
  private Boolean isActive;
}
