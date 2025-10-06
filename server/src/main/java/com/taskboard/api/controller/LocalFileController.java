package com.taskboard.api.controller;

import com.taskboard.api.service.LocalStorageService;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Контроллер для раздачи локальных файлов (аватаров). Используется когда MinIO/S3 недоступны. */
@RestController
@RequestMapping("/api/avatars/file")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class LocalFileController {

  private final LocalStorageService localStorageService;

  /** Получает файл аватара */
  @GetMapping("/{storageKey}")
  public ResponseEntity<InputStreamResource> getFile(@PathVariable String storageKey) {
    try {
      log.debug("Serving local file: {}", storageKey);

      if (!localStorageService.fileExists(storageKey)) {
        log.warn("File not found: {}", storageKey);
        return ResponseEntity.notFound().build();
      }

      InputStream inputStream = localStorageService.getFile(storageKey);
      var metadata = localStorageService.getFileMetadata(storageKey);

      String contentType = metadata.get("contentType");
      if (contentType == null || contentType.isEmpty()) {
        contentType = "application/octet-stream";
      }

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.parseMediaType(contentType));
      headers.setCacheControl("public, max-age=3600"); // Кэширование на 1 час
      headers.set("Content-Disposition", "inline");

      return ResponseEntity.ok().headers(headers).body(new InputStreamResource(inputStream));

    } catch (Exception e) {
      log.error("Error serving file: {}", storageKey, e);
      return ResponseEntity.internalServerError().build();
    }
  }

  /** Проверяет существование файла */
  @GetMapping("/{storageKey}/exists")
  public ResponseEntity<Boolean> fileExists(@PathVariable String storageKey) {
    try {
      boolean exists = localStorageService.fileExists(storageKey);
      return ResponseEntity.ok(exists);
    } catch (Exception e) {
      log.error("Error checking file existence: {}", storageKey, e);
      return ResponseEntity.ok(false);
    }
  }

  /** Получает метаданные файла */
  @GetMapping("/{storageKey}/metadata")
  public ResponseEntity<Object> getFileMetadata(@PathVariable String storageKey) {
    try {
      if (!localStorageService.fileExists(storageKey)) {
        return ResponseEntity.notFound().build();
      }

      var metadata = localStorageService.getFileMetadata(storageKey);
      return ResponseEntity.ok(metadata);

    } catch (Exception e) {
      log.error("Error getting file metadata: {}", storageKey, e);
      return ResponseEntity.internalServerError().build();
    }
  }
}
