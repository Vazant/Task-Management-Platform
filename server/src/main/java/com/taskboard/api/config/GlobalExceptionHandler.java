package com.taskboard.api.config;

import com.taskboard.api.dto.ApiResponse;
import com.taskboard.api.exception.EmailAlreadyExistsException;
import com.taskboard.api.exception.RegistrationException;
import com.taskboard.api.exception.UsernameAlreadyExistsException;
import com.taskboard.api.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MessageService messageService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        System.err.println("Validation errors: " + errors);
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(errors, messageService.getMessage("validation.request.invalid"), false));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(null, ex.getMessage(), false));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(null, ex.getMessage(), false));
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(null, ex.getMessage(), false));
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<ApiResponse<String>> handleRegistrationException(RegistrationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(null, ex.getMessage(), false));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = messageService.getMessage("error.data.integrity");

        // Проверяем, связана ли ошибка с дублированием email или username
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("email") || ex.getMessage().contains("EMAIL")) {
                message = messageService.getMessage("error.data.integrity.email_exists");
            } else if (ex.getMessage().contains("username") || ex.getMessage().contains("USERNAME")) {
                message = messageService.getMessage("error.data.integrity.username_exists");
            }
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(null, message, false));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(null, messageService.getMessage("error.global.unexpected", ex.getMessage()), false));
    }
}
