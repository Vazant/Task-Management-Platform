package com.taskboard.userservice.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Security tests for password hashing and validation.
 * Tests BCrypt password encoding, strength validation, and security measures.
 * 
 * @author TaskBoard Team
 * @version 1.0
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
class PasswordSecurityTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Should hash passwords using BCrypt")
    void shouldHashPasswordsUsingBcrypt() {
        String rawPassword = "password123";
        
        String hashedPassword = passwordEncoder.encode(rawPassword);
        
        assertNotNull(hashedPassword);
        assertNotEquals(rawPassword, hashedPassword);
        assertTrue(hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$"));
        assertTrue(hashedPassword.length() > 50); // BCrypt hashes are typically 60 characters
    }

    @Test
    @DisplayName("Should verify correct passwords")
    void shouldVerifyCorrectPasswords() {
        String rawPassword = "password123";
        String hashedPassword = passwordEncoder.encode(rawPassword);
        
        assertTrue(passwordEncoder.matches(rawPassword, hashedPassword));
    }

    @Test
    @DisplayName("Should reject incorrect passwords")
    void shouldRejectIncorrectPasswords() {
        String rawPassword = "password123";
        String wrongPassword = "wrongpassword";
        String hashedPassword = passwordEncoder.encode(rawPassword);
        
        assertFalse(passwordEncoder.matches(wrongPassword, hashedPassword));
    }

    @Test
    @DisplayName("Should generate different hashes for same password")
    void shouldGenerateDifferentHashesForSamePassword() {
        String rawPassword = "password123";
        
        String hash1 = passwordEncoder.encode(rawPassword);
        String hash2 = passwordEncoder.encode(rawPassword);
        
        assertNotEquals(hash1, hash2);
        assertTrue(passwordEncoder.matches(rawPassword, hash1));
        assertTrue(passwordEncoder.matches(rawPassword, hash2));
    }

    @Test
    @DisplayName("Should handle empty passwords securely")
    void shouldHandleEmptyPasswordsSecurely() {
        String emptyPassword = "";
        String hashedPassword = passwordEncoder.encode(emptyPassword);
        
        assertNotNull(hashedPassword);
        assertNotEquals(emptyPassword, hashedPassword);
        assertTrue(passwordEncoder.matches(emptyPassword, hashedPassword));
    }

    @Test
    @DisplayName("Should handle null passwords securely")
    void shouldHandleNullPasswordsSecurely() {
        assertThrows(IllegalArgumentException.class, () -> {
            passwordEncoder.encode(null);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            passwordEncoder.matches(null, "somehash");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            passwordEncoder.matches("password", null);
        });
    }

    @Test
    @DisplayName("Should handle very long passwords")
    void shouldHandleVeryLongPasswords() {
        StringBuilder longPassword = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longPassword.append("a");
        }
        
        String hashedPassword = passwordEncoder.encode(longPassword.toString());
        
        assertNotNull(hashedPassword);
        assertTrue(passwordEncoder.matches(longPassword.toString(), hashedPassword));
    }

    @Test
    @DisplayName("Should handle passwords with special characters")
    void shouldHandlePasswordsWithSpecialCharacters() {
        String specialPassword = "P@ssw0rd!@#$%^&*()_+-=[]{}|;':\",./<>?";
        
        String hashedPassword = passwordEncoder.encode(specialPassword);
        
        assertNotNull(hashedPassword);
        assertTrue(passwordEncoder.matches(specialPassword, hashedPassword));
    }

    @Test
    @DisplayName("Should handle Unicode passwords")
    void shouldHandleUnicodePasswords() {
        String unicodePassword = "пароль123🔐";
        
        String hashedPassword = passwordEncoder.encode(unicodePassword);
        
        assertNotNull(hashedPassword);
        assertTrue(passwordEncoder.matches(unicodePassword, hashedPassword));
    }

    @Test
    @DisplayName("Should use appropriate BCrypt strength")
    void shouldUseAppropriateBcryptStrength() {
        String password = "password123";
        String hashedPassword = passwordEncoder.encode(password);
        
        // Check that we're using a reasonable strength (10+ rounds)
        assertTrue(hashedPassword.contains("$2a$10$") || 
                  hashedPassword.contains("$2a$11$") || 
                  hashedPassword.contains("$2a$12$") ||
                  hashedPassword.contains("$2b$10$") || 
                  hashedPassword.contains("$2b$11$") || 
                  hashedPassword.contains("$2b$12$"));
    }

    @Test
    @DisplayName("Should be resistant to timing attacks")
    void shouldBeResistantToTimingAttacks() {
        String password = "password123";
        String hashedPassword = passwordEncoder.encode(password);
        
        // Test that verification takes similar time regardless of password correctness
        long startTime = System.nanoTime();
        passwordEncoder.matches(password, hashedPassword);
        long correctTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        passwordEncoder.matches("wrongpassword", hashedPassword);
        long incorrectTime = System.nanoTime() - startTime;
        
        // The times should be similar (within reasonable bounds)
        // This is a basic test - in production, more sophisticated timing attack tests would be needed
        assertTrue(Math.abs(correctTime - incorrectTime) < 1000000); // 1ms tolerance
    }

    @Test
    @DisplayName("Should handle common weak passwords")
    void shouldHandleCommonWeakPasswords() {
        String[] weakPasswords = {
            "123456",
            "password",
            "123456789",
            "qwerty",
            "abc123",
            "password123",
            "admin",
            "letmein"
        };
        
        for (String weakPassword : weakPasswords) {
            String hashedPassword = passwordEncoder.encode(weakPassword);
            assertNotNull(hashedPassword);
            assertTrue(passwordEncoder.matches(weakPassword, hashedPassword));
        }
    }

    @Test
    @DisplayName("Should handle passwords with leading/trailing whitespace")
    void shouldHandlePasswordsWithWhitespace() {
        String passwordWithSpaces = "  password123  ";
        String trimmedPassword = "password123";
        
        String hashedPassword = passwordEncoder.encode(passwordWithSpaces);
        
        assertNotNull(hashedPassword);
        assertTrue(passwordEncoder.matches(passwordWithSpaces, hashedPassword));
        assertFalse(passwordEncoder.matches(trimmedPassword, hashedPassword));
    }

    @Test
    @DisplayName("Should handle case-sensitive passwords")
    void shouldHandleCaseSensitivePasswords() {
        String lowercasePassword = "password123";
        String uppercasePassword = "PASSWORD123";
        String mixedCasePassword = "Password123";
        
        String lowercaseHash = passwordEncoder.encode(lowercasePassword);
        String uppercaseHash = passwordEncoder.encode(uppercasePassword);
        String mixedCaseHash = passwordEncoder.encode(mixedCasePassword);
        
        // All should be different
        assertNotEquals(lowercaseHash, uppercaseHash);
        assertNotEquals(lowercaseHash, mixedCaseHash);
        assertNotEquals(uppercaseHash, mixedCaseHash);
        
        // Each should only match its own case
        assertTrue(passwordEncoder.matches(lowercasePassword, lowercaseHash));
        assertFalse(passwordEncoder.matches(uppercasePassword, lowercaseHash));
        assertFalse(passwordEncoder.matches(mixedCasePassword, lowercaseHash));
    }

    @Test
    @DisplayName("Should handle concurrent password encoding")
    void shouldHandleConcurrentPasswordEncoding() throws InterruptedException {
        String password = "password123";
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        String[] hashes = new String[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                hashes[index] = passwordEncoder.encode(password);
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        // All hashes should be different and valid
        for (int i = 0; i < threadCount; i++) {
            assertNotNull(hashes[i]);
            assertTrue(passwordEncoder.matches(password, hashes[i]));
            
            // Check that all hashes are different
            for (int j = i + 1; j < threadCount; j++) {
                assertNotEquals(hashes[i], hashes[j]);
            }
        }
    }
}
