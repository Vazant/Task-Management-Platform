package com.taskboard.api.controller;

import com.taskboard.api.dto.ApiResponse;
import com.taskboard.api.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/auth")
    public ResponseEntity<ApiResponse<String>> testAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Test auth endpoint called. Authentication: {}", auth);

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return ResponseEntity.ok(new ApiResponse<>(
                messageService.getMessage("info.test.auth", auth.getName()),
                "Success",
                true));
        } else {
            return ResponseEntity.ok(new ApiResponse<>(
                messageService.getMessage("info.test.auth_failed"),
                "No valid authentication",
                false));
        }
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<String>> testPublic(@RequestParam(value = "lang", required = false) String lang) {
        log.info("Test public endpoint called with lang: {}", lang);
        // LocaleChangeInterceptor автоматически обрабатывает параметр lang
        String message = messageService.getMessage("info.test.public");
        return ResponseEntity.ok(new ApiResponse<>(
            message,
            "Success",
            true));
    }
}
