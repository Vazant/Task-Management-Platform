package com.taskboard.api.controller;

import com.taskboard.api.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestController {

    @GetMapping("/auth")
    public ResponseEntity<ApiResponse<String>> testAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Test auth endpoint called. Authentication: {}", auth);

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return ResponseEntity.ok(new ApiResponse<>(
                "Authenticated as: " + auth.getName(),
                "Success",
                true));
        } else {
            return ResponseEntity.ok(new ApiResponse<>(
                "Not authenticated",
                "No valid authentication",
                false));
        }
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<String>> testPublic() {
        log.info("Test public endpoint called");
        return ResponseEntity.ok(new ApiResponse<>(
            "Public endpoint works",
            "Success",
            true));
    }
}
