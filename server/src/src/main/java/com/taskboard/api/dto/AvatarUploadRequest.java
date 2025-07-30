package com.taskboard.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO для запроса загрузки аватара.
 */
@Data
public class AvatarUploadRequest {

    @NotBlank(message = "Имя файла обязательно")
    @Size(max = 255, message = "Имя файла не должно превышать 255 символов")
    private String fileName;

    @NotBlank(message = "Тип контента обязателен")
    @Pattern(regexp = "^image/(jpeg|jpg|png|gif|webp)$",
             message = "Поддерживаются только изображения: JPEG, PNG, GIF, WebP")
    private String contentType;

    @Size(max = 10, message = "Размер файла не должен превышать 10 МБ")
    private Long fileSize;
}
