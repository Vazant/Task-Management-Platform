package com.taskboard.api.service;

import com.taskboard.api.config.CustomUserDetailsService;
import com.taskboard.api.dto.LoginRequest;
import com.taskboard.api.dto.LoginResponse;
import com.taskboard.api.dto.RegisterRequest;
import com.taskboard.api.exception.EmailAlreadyExistsException;
import com.taskboard.api.exception.RegistrationException;
import com.taskboard.api.exception.UsernameAlreadyExistsException;
import com.taskboard.api.model.User;
import com.taskboard.api.model.UserRole;
import com.taskboard.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AvatarService avatarService;

    public LoginResponse login(LoginRequest request) {
        // Аутентификация
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Получение пользователя
        User user = (User) userDetailsService.loadUserByUsername(request.getEmail());

        // Обновление времени последнего входа
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Генерация токенов
        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponse(user, token, refreshToken);
    }

    public LoginResponse register(RegisterRequest request) {
        // Проверка паролей
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RegistrationException("auth.register.failure.passwords_mismatch");
        }

        // Проверка существования пользователя
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("auth.register.failure.email_exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("auth.register.failure.username_exists");
        }

        // Создание нового пользователя
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);
        user.setCreatedAt(LocalDateTime.now());

        try {
            user = userRepository.save(user);

            // TODO: Implement avatar generation from initials
            // String avatarUrl = avatarService.generateAvatarFromInitials(request.getUsername(), user.getId());
            // user.setAvatar(avatarUrl);
            // userRepository.save(user);

        } catch (DataIntegrityViolationException ex) {
            // Дополнительная проверка на случай race condition
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyExistsException("auth.register.failure.email_exists", ex);
            }
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new UsernameAlreadyExistsException("auth.register.failure.username_exists", ex);
            }
            throw new RegistrationException("auth.register.failure.generic", ex);
        }

        // Генерация токенов
        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponse(user, token, refreshToken);
    }

    public String refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtService.validateToken(refreshToken, userDetails)) {
            return jwtService.generateToken(userDetails);
        }

        throw new RuntimeException("auth.refresh.failure.invalid");
    }
}
