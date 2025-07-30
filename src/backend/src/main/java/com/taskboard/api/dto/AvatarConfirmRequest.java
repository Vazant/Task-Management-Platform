package com.taskboard.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO для подтверждения загрузки аватара.
 */
@Data
public class AvatarConfirmRequest {

    @NotBlank(message = "Storage key обязателен")
    private String storageKey;
}
