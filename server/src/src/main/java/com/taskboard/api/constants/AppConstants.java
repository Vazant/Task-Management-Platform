package com.taskboard.api.constants;

/**
 * Константы приложения.
 */
public final class AppConstants {
    
    private AppConstants() {
        // Utility class
    }
    
    // User constraints
    public static final int USERNAME_MIN_LENGTH = 3;
    public static final int USERNAME_MAX_LENGTH = 20;
    public static final int PASSWORD_MIN_LENGTH = 8;
    
    // File upload constraints
    public static final int MAX_FILE_SIZE_MB = 5;
    public static final long MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MB * 1024L * 1024L;
    
    // Paths
    public static final String AVATAR_UPLOAD_DIR = "uploads/avatars/";
    
    // Message keys
    public static final class Messages {
        // Validation
        public static final String VALIDATION_EMAIL_REQUIRED = "validation.email.required";
        public static final String VALIDATION_EMAIL_INVALID = "validation.email.invalid";
        public static final String VALIDATION_USERNAME_REQUIRED = "validation.username.required";
        public static final String VALIDATION_USERNAME_SIZE = "validation.username.size";
        public static final String VALIDATION_PASSWORD_REQUIRED = "validation.password.required";
        public static final String VALIDATION_PASSWORD_SIZE = "validation.password.size";
        public static final String VALIDATION_PASSWORD_CONFIRM_REQUIRED = "validation.password.confirm.required";
        
        // Authentication
        public static final String AUTH_LOGIN_SUCCESS = "auth.login.success";
        public static final String AUTH_REGISTER_SUCCESS = "auth.register.success";
        public static final String AUTH_REFRESH_REQUIRED = "auth.refresh.required";
        public static final String AUTH_TOKEN_REFRESHED = "auth.token.refreshed";
        public static final String AUTH_PASSWORD_RESET_SENT = "auth.password.reset.sent";
        public static final String AUTH_PASSWORD_RESET_INSTRUCTION = "auth.password.reset.instruction";
        public static final String AUTH_PASSWORD_RESET_SUCCESS = "auth.password.reset.success";
        public static final String AUTH_PASSWORD_CHANGED = "auth.password.changed";
        public static final String AUTH_TOKEN_REQUIRED = "auth.token.required";
        public static final String AUTH_NEWPASSWORD_REQUIRED = "auth.newpassword.required";
        
        // Profile
        public static final String PROFILE_LOADED = "profile.loaded";
        public static final String PROFILE_UPDATED = "profile.updated";
        public static final String PROFILE_AVATAR_UPDATED = "profile.avatar.updated";
        public static final String PROFILE_AVATAR_DELETED = "profile.avatar.deleted";
        public static final String PROFILE_AVATAR_UPDATE_ERROR = "profile.avatar.update.error";
        public static final String PROFILE_AVATAR_DELETE_ERROR = "profile.avatar.delete.error";
        public static final String PROFILE_PASSWORD_CHANGE_ERROR = "profile.password.change.error";
        
        // Errors
        public static final String ERROR_USER_NOTFOUND = "error.user.notfound";
        public static final String ERROR_USER_EMAIL_EXISTS = "error.user.email.exists";
        public static final String ERROR_USER_USERNAME_EXISTS = "error.user.username.exists";
        public static final String ERROR_PASSWORD_MISMATCH = "error.password.mismatch";
        public static final String ERROR_PASSWORD_INCORRECT = "error.password.incorrect";
        public static final String ERROR_PASSWORD_SAME = "error.password.same";
        public static final String ERROR_AUTH_INVALID = "error.auth.invalid";
        public static final String ERROR_VALIDATION = "error.validation";
        public static final String ERROR_INTERNAL = "error.internal";
        public static final String ERROR_FILE_EMPTY = "error.file.empty";
        public static final String ERROR_FILE_TOOBIG = "error.file.toobig";
        public static final String ERROR_FILE_TYPE = "error.file.type";
        public static final String ERROR_AVATAR_NOTFOUND = "error.avatar.notfound";
        
        // Success
        public static final String SUCCESS_GENERIC = "success.generic";
        
        private Messages() {
            // Utility class
        }
    }
}