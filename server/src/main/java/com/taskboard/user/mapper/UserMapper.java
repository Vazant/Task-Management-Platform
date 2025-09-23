package com.taskboard.user.mapper;

import com.taskboard.api.dto.LoginResponse;
import com.taskboard.api.dto.RegisterRequest;
import com.taskboard.api.dto.UpdateProfileRequest;
import com.taskboard.api.model.User;
import com.taskboard.user.dto.*;
import com.taskboard.user.model.NotificationSettings;
import com.taskboard.user.model.UserEntity;
import com.taskboard.user.model.UserPreferences;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct mapper for user-related entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Convert UserEntity to UserDto.
     *
     * @param entity the user entity
     * @return the user DTO
     */
    UserDto toDto(UserEntity entity);

    /**
     * Convert UserEntity to UserProfileDto.
     *
     * @param entity the user entity
     * @return the user profile DTO
     */
    @Mapping(target = "role", expression = "java(entity.getRole().name())")
    UserProfileDto toProfileDto(UserEntity entity);

    /**
     * Convert UserEntity to User (existing API model).
     *
     * @param entity the user entity
     * @return the user model
     */
    @Mapping(target = "role", expression = "java(mapToApiUserRole(entity.getRole()))")
    @Mapping(target = "authorities", ignore = true)
    User toApiUser(UserEntity entity);

    /**
     * Map UserRole to API UserRole.
     *
     * @param role the user role
     * @return the API user role
     */
    default com.taskboard.api.model.UserRole mapToApiUserRole(com.taskboard.user.model.UserRole role) {
        return switch (role) {
            case USER -> com.taskboard.api.model.UserRole.USER;
            case ADMIN -> com.taskboard.api.model.UserRole.ADMIN;
        };
    }

    /**
     * Convert RegisterRequest to UserEntity.
     *
     * @param request the register request
     * @return the user entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // Password should be encoded separately
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "preferences", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "accountNonExpired", constant = "true")
    @Mapping(target = "accountNonLocked", constant = "true")
    @Mapping(target = "credentialsNonExpired", constant = "true")
    @Mapping(target = "passwordResetToken", ignore = true)
    @Mapping(target = "passwordResetTokenExpiry", ignore = true)
    UserEntity fromRegisterRequest(RegisterRequest request);

    /**
     * Update UserEntity from UpdateProfileRequest.
     *
     * @param request the update profile request
     * @param entity the user entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "preferences", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "passwordResetToken", ignore = true)
    @Mapping(target = "passwordResetTokenExpiry", ignore = true)
    void updateEntityFromProfileRequest(UpdateProfileRequest request, @MappingTarget UserEntity entity);

    /**
     * Convert UserPreferences to UserPreferencesDto.
     *
     * @param preferences the user preferences
     * @return the user preferences DTO
     */
    UserPreferencesDto toPreferencesDto(UserPreferences preferences);

    /**
     * Convert NotificationSettings to NotificationSettingsDto.
     *
     * @param settings the notification settings
     * @return the notification settings DTO
     */
    NotificationSettingsDto toNotificationSettingsDto(NotificationSettings settings);

    /**
     * Create LoginResponse from UserEntity and tokens.
     *
     * @param entity the user entity
     * @param token the access token
     * @param refreshToken the refresh token
     * @return the login response
     */
    @Mapping(target = "user", expression = "java(toApiUser(entity))")
    @Mapping(target = "token", source = "token")
    @Mapping(target = "refreshToken", source = "refreshToken")
    LoginResponse toLoginResponse(UserEntity entity, String token, String refreshToken);
}
