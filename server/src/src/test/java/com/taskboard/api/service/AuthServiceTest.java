package com.taskboard.api.service;

import com.taskboard.api.config.CustomUserDetailsService;
import com.taskboard.api.dto.RegisterRequest;
import com.taskboard.api.exception.EmailAlreadyExistsException;
import com.taskboard.api.exception.UsernameAlreadyExistsException;
import com.taskboard.api.model.User;
import com.taskboard.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest validRequest;
    private User existingUser;

    @BeforeEach
    void setUp() {
        validRequest = new RegisterRequest();
        validRequest.setEmail("test@example.com");
        validRequest.setUsername("testuser");
        validRequest.setPassword("password123");
        validRequest.setConfirmPassword("password123");

        existingUser = new User();
        existingUser.setEmail("existing@example.com");
        existingUser.setUsername("existinguser");
    }

    @Test
    void register_WithValidData_ShouldCreateUser() {
        // Arrange
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(validRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

        // Act
        var result = authService.register(validRequest);

        // Assert
        assertNotNull(result);
        assertEquals("token", result.getToken());
        assertEquals("refreshToken", result.getRefreshToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_WithExistingEmail_ShouldThrowEmailAlreadyExistsException() {
        // Arrange
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(true);

        // Act & Assert
        EmailAlreadyExistsException exception = assertThrows(
            EmailAlreadyExistsException.class,
            () -> authService.register(validRequest)
        );
        assertEquals("Пользователь с таким email уже существует", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_WithExistingUsername_ShouldThrowUsernameAlreadyExistsException() {
        // Arrange
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(validRequest.getUsername())).thenReturn(true);

        // Act & Assert
        UsernameAlreadyExistsException exception = assertThrows(
            UsernameAlreadyExistsException.class,
            () -> authService.register(validRequest)
        );
        assertEquals("Пользователь с таким именем уже существует", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_WithMismatchedPasswords_ShouldThrowRegistrationException() {
        // Arrange
        validRequest.setConfirmPassword("differentPassword");

        // Act & Assert
        var exception = assertThrows(
            com.taskboard.api.exception.RegistrationException.class,
            () -> authService.register(validRequest)
        );
        assertEquals("Пароли не совпадают", exception.getMessage());
        verify(userRepository, never()).save(any());
    }
}
