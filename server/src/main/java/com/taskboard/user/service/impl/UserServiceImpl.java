package com.taskboard.user.service.impl;

import com.taskboard.api.dto.*;
import com.taskboard.api.service.JwtService;
import com.taskboard.api.service.MessageService;
import com.taskboard.user.dto.*;
import com.taskboard.user.mapper.UserMapper;
import com.taskboard.user.model.UserEntity;
import com.taskboard.user.model.UserRole;
import com.taskboard.user.repository.UserRepository;
import com.taskboard.user.service.EmailService;
import com.taskboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of UserService.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MessageService messageService;
    private final EmailService emailService;

    @Value("${password.reset.token.expiration:3600000}")
    private long passwordResetTokenExpiration;

    @Value("${password.reset.url.base}")
    private String passwordResetUrlBase;

    @Override
    public LoginResponse login(LoginRequest request) {
        log.debug("Login attempt for email: {}", request.getEmail());

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException(
                        messageService.getMessage("auth.error.invalid.credentials")));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password attempt for user: {}", user.getEmail());
            throw new BadCredentialsException(
                    messageService.getMessage("auth.error.invalid.credentials"));
        }

        if (!user.isEnabled()) {
            throw new BadCredentialsException(
                    messageService.getMessage("auth.error.account.disabled"));
        }

        if (!user.isAccountNonLocked()) {
            throw new BadCredentialsException(
                    messageService.getMessage("auth.error.account.locked"));
        }

        // Update last login
        user.setLastLogin(new Date());
        userRepository.save(user);

        // Generate tokens
        String token = jwtService.generateToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        log.info("User {} logged in successfully", user.getEmail());

        return userMapper.toLoginResponse(user, token, refreshToken);
    }

    @Override
    public LoginResponse register(RegisterRequest request) {
        log.debug("Registration attempt for email: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    messageService.getMessage("auth.error.email.exists"));
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(
                    messageService.getMessage("auth.error.username.exists"));
        }

        // Validate password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException(
                    messageService.getMessage("auth.error.password.mismatch"));
        }

        // Create new user
        UserEntity user = userMapper.fromRegisterRequest(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);
        user.setLastLogin(new Date());

        user = userRepository.save(user);

        // Generate tokens
        String token = jwtService.generateToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        log.info("New user registered: {}", user.getEmail());

        return userMapper.toLoginResponse(user, token, refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getProfile(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        messageService.getMessage("user.error.not.found")));

        return userMapper.toProfileDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getCurrentUserProfile(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        messageService.getMessage("user.error.not.found")));

        return userMapper.toProfileDto(user);
    }

    @Override
    public UserProfileDto updateProfile(String userId, UpdateProfileRequest request) {
        log.debug("Updating profile for user: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        messageService.getMessage("user.error.not.found")));

        // Check if email is being changed and already exists
        if (!user.getEmail().equals(request.getEmail()) &&
            userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    messageService.getMessage("auth.error.email.exists"));
        }

        // Check if username is being changed and already exists
        if (!user.getUsername().equals(request.getUsername()) &&
            userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(
                    messageService.getMessage("auth.error.username.exists"));
        }

        // Update user fields
        userMapper.updateEntityFromProfileRequest(request, user);
        user = userRepository.save(user);

        log.info("Profile updated for user: {}", user.getEmail());

        return userMapper.toProfileDto(user);
    }

    @Override
    public void changePassword(String userId, ChangePasswordRequest request) {
        log.debug("Password change request for user: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        messageService.getMessage("user.error.not.found")));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException(
                    messageService.getMessage("auth.error.invalid.current.password"));
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed for user: {}", user.getEmail());
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String username = jwtService.extractUsername(request.getRefreshToken());

        if (username == null || !jwtService.validateToken(request.getRefreshToken(), username)) {
            throw new BadCredentialsException(
                    messageService.getMessage("auth.error.invalid.refresh.token"));
        }

        // Generate new tokens
        String newToken = jwtService.generateToken(username);
        String newRefreshToken = jwtService.generateRefreshToken(username);

        return RefreshTokenResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        log.debug("Password reset request for email: {}", request.getEmail());

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(
                        messageService.getMessage("user.error.not.found")));

        // Generate reset token
        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(new Date(System.currentTimeMillis() + passwordResetTokenExpiration));
        userRepository.save(user);

        // Send email
        String resetUrl = passwordResetUrlBase + "?token=" + resetToken;
        emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), resetUrl);

        log.info("Password reset email sent to: {}", user.getEmail());

        return MessageResponse.of(messageService.getMessage("auth.password.reset.email.sent"));
    }

    @Override
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        log.debug("Password reset with token: {}", request.getToken());

        UserEntity user = userRepository.findByPasswordResetToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException(
                        messageService.getMessage("auth.error.invalid.reset.token")));

        // Check token expiry
        if (user.getPasswordResetTokenExpiry() == null ||
            user.getPasswordResetTokenExpiry().before(new Date())) {
            throw new IllegalArgumentException(
                    messageService.getMessage("auth.error.expired.reset.token"));
        }

        // Update password and clear reset token
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);

        log.info("Password reset successful for user: {}", user.getEmail());

        return MessageResponse.of(messageService.getMessage("auth.password.reset.success"));
    }

    @Override
    public UserProfileDto updateAvatar(String userId, String avatarUrl) {
        log.debug("Updating avatar for user: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        messageService.getMessage("user.error.not.found")));

        user.setAvatar(avatarUrl);
        user = userRepository.save(user);

        log.info("Avatar updated for user: {}", user.getEmail());

        return userMapper.toProfileDto(user);
    }

    @Override
    public UserProfileDto deleteAvatar(String userId) {
        log.debug("Deleting avatar for user: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        messageService.getMessage("user.error.not.found")));

        user.setAvatar(null);
        user = userRepository.save(user);

        log.info("Avatar deleted for user: {}", user.getEmail());

        return userMapper.toProfileDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        messageService.getMessage("user.error.not.found")));

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // Admin operations

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers(int page, int size) {
        log.debug("Getting all users: page={}, size={}", page, size);
        return userRepository.findAll()
                .stream()
                .skip((long) page * size)
                .limit(size)
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getUserById(String userId) {
        log.debug("Getting user by ID: {}", userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
        return userMapper.toProfileDto(user);
    }

    @Override
    public UserProfileDto createUser(UserDto userDto) {
        log.debug("Creating new user: {}", userDto.getEmail());

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("User with email already exists: " + userDto.getEmail());
        }

        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("User with username already exists: " + userDto.getUsername());
        }

        // Create new user entity manually since UserDto doesn't have all required fields
        UserEntity user = new UserEntity();
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setRole(userDto.getRole());
        user.setAvatar(userDto.getAvatar());
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setCreatedAt(new Date());

        UserEntity savedUser = userRepository.save(user);
        log.info("User created successfully: {}", savedUser.getEmail());

        return userMapper.toProfileDto(savedUser);
    }

    @Override
    public UserProfileDto updateUser(String userId, UserDto userDto) {
        log.debug("Updating user: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

        // Check if email is being changed and if it's already taken
        if (!user.getEmail().equals(userDto.getEmail()) &&
            userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userDto.getEmail());
        }

        // Check if username is being changed and if it's already taken
        if (!user.getUsername().equals(userDto.getUsername()) &&
            userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + userDto.getUsername());
        }

        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setRole(userDto.getRole());
        user.setAvatar(userDto.getAvatar());
        user.setUpdatedAt(new Date());

        UserEntity savedUser = userRepository.save(user);
        log.info("User updated successfully: {}", savedUser.getEmail());

        return userMapper.toProfileDto(savedUser);
    }

    @Override
    public void deleteUser(String userId) {
        log.debug("Deleting user: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

        userRepository.delete(user);
        log.info("User deleted successfully: {}", user.getEmail());
    }

    @Override
    public UserProfileDto toggleUserBlock(String userId) {
        log.debug("Toggling user block: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

        user.setAccountNonLocked(!user.isAccountNonLocked());
        user.setUpdatedAt(new Date());

        UserEntity savedUser = userRepository.save(user);
        log.info("User block status toggled: {} -> {}", user.getEmail(), savedUser.isAccountNonLocked());

        return userMapper.toProfileDto(savedUser);
    }

    @Override
    public UserProfileDto updateUserRole(String userId, String role) {
        log.debug("Updating user role: userId={}, role={}", userId, role);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

        try {
            UserRole newRole = UserRole.valueOf(role.toUpperCase());
            user.setRole(newRole);
            user.setUpdatedAt(new Date());

            UserEntity savedUser = userRepository.save(user);
            log.info("User role updated: {} -> {}", user.getEmail(), newRole);

            return userMapper.toProfileDto(savedUser);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    /**
     * Search users by email or username.
     * This method is designed for extension and can be safely overridden by subclasses.
     * When overriding, ensure to maintain the same search behavior and return type.
     *
     * @param query the search query string
     * @return list of matching users
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> searchUsers(final String query) {
        log.debug("Searching users: {}", query);

        return userRepository.findByEmailContainingIgnoreCaseOrUsernameContainingIgnoreCase(
                query, query)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }
}
