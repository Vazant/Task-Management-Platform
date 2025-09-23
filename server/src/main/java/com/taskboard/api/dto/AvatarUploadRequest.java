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

    @NotBlank(message = "{validation.file.name.required}")
    @Size(max = 255, message = "{validation.file.name.size}")
    private String fileName;

    @NotBlank(message = "{validation.file.content_type.required}")
    @Pattern(regexp = "^image/(jpeg|jpg|png|gif|webp)$",
             message = "{validation.file.content_type.invalid}")
    private String contentType;

    @Size(max = 10, message = "{validation.file.size.max}")
    private Long fileSize;
}
