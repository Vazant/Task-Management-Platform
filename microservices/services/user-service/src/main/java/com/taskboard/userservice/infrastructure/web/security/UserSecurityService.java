package com.taskboard.userservice.infrastructure.web.security;

import com.taskboard.userservice.application.dto.GetUserRequest;
import com.taskboard.userservice.application.usecase.GetUserUseCase;
import com.taskboard.userservice.application.dto.GetUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Security service for user access control and authorization checks.
 * Provides methods for verifying user permissions and ownership of resources.
 * 
 * <p>This service handles various security-related operations including:
 * <ul>
 *   <li>Resource ownership verification</li>
 *   <li>Role-based access control checks</li>
 *   <li>Current user information retrieval</li>
 *   <li>Authentication status verification</li>
 * </ul>
 * 
 * <p>Used primarily in Spring Security expressions and controller methods
 * to enforce fine-grained access control.
 * 
 * @author TaskBoard Team
 * @version 1.0
 * @since 1.0.0
 * @see org.springframework.security.access.prepost.PreAuthorize
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserSecurityService {

    private final GetUserUseCase getUserUseCase;

    /**
     * Checks if the current user is the owner of the specified resource.
     * 
     * <p>This method is used in Spring Security expressions to verify
     * that a user can only access their own resources.
     * 
     * @param userId the ID of the user who owns the resource
     * @param authentication the current authentication context
     * @return true if the current user is the owner, false otherwise
     */
    public boolean isOwner(Long userId, Authentication authentication) {
        try {
            String currentUsername = authentication.getName();
            
            // Получаем информацию о пользователе-владельце
            GetUserRequest request = GetUserRequest.builder()
                    .userId(userId)
                    .build();
            
            GetUserResponse userResponse = getUserUseCase.execute(request);
            
            // Проверяем, совпадает ли имя пользователя
            boolean isOwner = currentUsername.equals(userResponse.getUsername());
            
            log.debug("Security check - User '{}' is owner of user '{}': {}", 
                    currentUsername, userResponse.getUsername(), isOwner);
            
            return isOwner;
            
        } catch (Exception e) {
            log.warn("Failed to check ownership for user {}: {}", userId, e.getMessage());
            return false;
        }
    }

    /**
     * Проверяет, является ли текущий пользователь администратором.
     *
     * @param authentication объект аутентификации
     * @return true, если текущий пользователь является администратором
     */
    public boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    /**
     * Проверяет, является ли текущий пользователь администратором или владельцем ресурса.
     *
     * @param userId ID пользователя-владельца ресурса
     * @param authentication объект аутентификации
     * @return true, если текущий пользователь является администратором или владельцем
     */
    public boolean isAdminOrOwner(Long userId, Authentication authentication) {
        return isAdmin(authentication) || isOwner(userId, authentication);
    }

    /**
     * Gets the ID of the currently authenticated user.
     * 
     * @return the current user's ID, or null if not found
     */
    public Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }

            String username = authentication.getName();
            
            // TODO: Реализовать поиск пользователя по username для получения ID
            // Это можно сделать через новый UseCase или расширить существующий
            
            log.debug("Current user ID for '{}': {}", username, "not implemented yet");
            return null;
            
        } catch (Exception e) {
            log.warn("Failed to get current user ID: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Получает имя текущего пользователя.
     *
     * @return имя текущего пользователя или null, если не найден
     */
    public String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }

            return authentication.getName();
            
        } catch (Exception e) {
            log.warn("Failed to get current username: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Проверяет, аутентифицирован ли текущий пользователь.
     *
     * @return true, если пользователь аутентифицирован
     */
    public boolean isAuthenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication != null && authentication.isAuthenticated() 
                    && !"anonymousUser".equals(authentication.getName());
            
        } catch (Exception e) {
            log.warn("Failed to check authentication status: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Проверяет, имеет ли текущий пользователь указанную роль.
     *
     * @param role роль для проверки
     * @return true, если пользователь имеет указанную роль
     */
    public boolean hasRole(String role) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return false;
            }

            String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> roleWithPrefix.equals(authority.getAuthority()));
            
        } catch (Exception e) {
            log.warn("Failed to check role '{}': {}", role, e.getMessage());
            return false;
        }
    }
}
