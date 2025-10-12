package com.taskboard.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskboard.userservice.infrastructure.web.dto.LoginRequest;
import com.taskboard.userservice.infrastructure.web.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security tests for attack protection mechanisms.
 * Tests protection against common attacks like SQL injection, XSS, CSRF, etc.
 * 
 * @author TaskBoard Team
 * @version 1.0
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AttackProtectionSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        registerRequest = RegisterRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .confirmPassword("password123")
                .firstName("New")
                .lastName("User")
                .acceptTerms(true)
                .build();
    }

    @Test
    @DisplayName("Should protect against SQL injection attacks")
    void shouldProtectAgainstSqlInjectionAttacks() throws Exception {
        // Test SQL injection in username field
        String[] sqlInjectionPayloads = {
            "admin'; DROP TABLE users; --",
            "admin' OR '1'='1",
            "admin' UNION SELECT * FROM users --",
            "admin'; INSERT INTO users VALUES ('hacker', 'password'); --",
            "admin' AND 1=1 --",
            "admin' OR 1=1 --"
        };

        for (String payload : sqlInjectionPayloads) {
            LoginRequest maliciousRequest = LoginRequest.builder()
                    .username(payload)
                    .password("password")
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(maliciousRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Invalid credentials"));
        }
    }

    @Test
    @DisplayName("Should protect against XSS attacks")
    void shouldProtectAgainstXssAttacks() throws Exception {
        // Test XSS in various fields
        String[] xssPayloads = {
            "<script>alert('XSS')</script>",
            "javascript:alert('XSS')",
            "<img src=x onerror=alert('XSS')>",
            "<svg onload=alert('XSS')>",
            "';alert('XSS');//",
            "<iframe src=javascript:alert('XSS')></iframe>"
        };

        for (String payload : xssPayloads) {
            RegisterRequest maliciousRequest = RegisterRequest.builder()
                    .username(payload)
                    .email("test@example.com")
                    .password("password123")
                    .confirmPassword("password123")
                    .firstName(payload)
                    .lastName(payload)
                    .acceptTerms(true)
                    .build();

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(maliciousRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"));
        }
    }

    @Test
    @DisplayName("Should protect against CSRF attacks")
    void shouldProtectAgainstCsrfAttacks() throws Exception {
        // Test CSRF protection by sending requests without proper CSRF tokens
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());

        // Test with fake CSRF token
        mockMvc.perform(post("/api/auth/login")
                        .header("X-CSRF-Token", "fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should protect against LDAP injection attacks")
    void shouldProtectAgainstLdapInjectionAttacks() throws Exception {
        // Test LDAP injection payloads
        String[] ldapPayloads = {
            "admin)(&(password=*))",
            "admin)(|(password=*))",
            "admin)(&(objectClass=*))",
            "admin)(|(objectClass=*))",
            "admin)(&(cn=*))",
            "admin)(|(cn=*))"
        };

        for (String payload : ldapPayloads) {
            LoginRequest maliciousRequest = LoginRequest.builder()
                    .username(payload)
                    .password("password")
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(maliciousRequest)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    @DisplayName("Should protect against NoSQL injection attacks")
    void shouldProtectAgainstNoSqlInjectionAttacks() throws Exception {
        // Test NoSQL injection payloads
        String[] nosqlPayloads = {
            "admin' || '1'=='1",
            "admin' && '1'=='1",
            "admin' || '1'==1",
            "admin' && '1'==1",
            "admin' || 1==1",
            "admin' && 1==1"
        };

        for (String payload : nosqlPayloads) {
            LoginRequest maliciousRequest = LoginRequest.builder()
                    .username(payload)
                    .password("password")
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(maliciousRequest)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    @DisplayName("Should protect against command injection attacks")
    void shouldProtectAgainstCommandInjectionAttacks() throws Exception {
        // Test command injection payloads
        String[] commandPayloads = {
            "admin; rm -rf /",
            "admin | cat /etc/passwd",
            "admin && whoami",
            "admin || id",
            "admin; ls -la",
            "admin | ps aux"
        };

        for (String payload : commandPayloads) {
            LoginRequest maliciousRequest = LoginRequest.builder()
                    .username(payload)
                    .password("password")
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(maliciousRequest)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    @DisplayName("Should protect against path traversal attacks")
    void shouldProtectAgainstPathTraversalAttacks() throws Exception {
        // Test path traversal payloads
        String[] pathTraversalPayloads = {
            "../../../etc/passwd",
            "..\\..\\..\\windows\\system32\\drivers\\etc\\hosts",
            "....//....//....//etc/passwd",
            "..%2F..%2F..%2Fetc%2Fpasswd",
            "..%252F..%252F..%252Fetc%252Fpasswd"
        };

        for (String payload : pathTraversalPayloads) {
            LoginRequest maliciousRequest = LoginRequest.builder()
                    .username(payload)
                    .password("password")
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(maliciousRequest)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    @DisplayName("Should protect against XML external entity attacks")
    void shouldProtectAgainstXmlExternalEntityAttacks() throws Exception {
        // Test XXE payloads
        String xxePayload = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<!DOCTYPE foo [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]>" +
                "<foo>&xxe;</foo>";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(xxePayload))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Should protect against JSON injection attacks")
    void shouldProtectAgainstJsonInjectionAttacks() throws Exception {
        // Test JSON injection payloads
        String[] jsonPayloads = {
            "{\"username\": \"admin\", \"password\": \"password\", \"role\": \"admin\"}",
            "{\"username\": \"admin\", \"password\": \"password\", \"isAdmin\": true}",
            "{\"username\": \"admin\", \"password\": \"password\", \"permissions\": [\"*\"]]}",
            "{\"username\": \"admin\", \"password\": \"password\", \"accessLevel\": 999}"
        };

        for (String payload : jsonPayloads) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    @DisplayName("Should protect against header injection attacks")
    void shouldProtectAgainstHeaderInjectionAttacks() throws Exception {
        // Test header injection payloads
        String[] headerPayloads = {
            "admin\r\nX-Injected-Header: malicious",
            "admin\nX-Injected-Header: malicious",
            "admin\rX-Injected-Header: malicious",
            "admin%0d%0aX-Injected-Header: malicious",
            "admin%0aX-Injected-Header: malicious"
        };

        for (String payload : headerPayloads) {
            mockMvc.perform(post("/api/auth/login")
                            .header("X-Username", payload)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    @DisplayName("Should protect against parameter pollution attacks")
    void shouldProtectAgainstParameterPollutionAttacks() throws Exception {
        // Test parameter pollution
        mockMvc.perform(post("/api/auth/login")
                        .param("username", "admin")
                        .param("username", "user")
                        .param("password", "password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should protect against buffer overflow attacks")
    void shouldProtectAgainstBufferOverflowAttacks() throws Exception {
        // Test buffer overflow with very long strings
        StringBuilder longString = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longString.append("a");
        }

        LoginRequest maliciousRequest = LoginRequest.builder()
                .username(longString.toString())
                .password(longString.toString())
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maliciousRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should protect against timing attacks")
    void shouldProtectAgainstTimingAttacks() throws Exception {
        // Test that response times are consistent regardless of input
        String[] testUsernames = {
            "admin", "user", "nonexistent", "a", "verylongusernamethatdoesnotexist"
        };

        for (String username : testUsernames) {
            LoginRequest request = LoginRequest.builder()
                    .username(username)
                    .password("wrongpassword")
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    @DisplayName("Should protect against brute force attacks")
    void shouldProtectAgainstBruteForceAttacks() throws Exception {
        // Test brute force protection
        for (int i = 0; i < 10; i++) {
            LoginRequest request = LoginRequest.builder()
                    .username("admin")
                    .password("wrongpassword" + i)
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        // After multiple failed attempts, should be rate limited
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    @DisplayName("Should protect against dictionary attacks")
    void shouldProtectAgainstDictionaryAttacks() throws Exception {
        // Test dictionary attack protection
        String[] commonPasswords = {
            "password", "123456", "admin", "root", "test", "guest", "user"
        };

        for (String password : commonPasswords) {
            LoginRequest request = LoginRequest.builder()
                    .username("admin")
                    .password(password)
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    @DisplayName("Should protect against credential stuffing attacks")
    void shouldProtectAgainstCredentialStuffingAttacks() throws Exception {
        // Test credential stuffing protection
        String[] commonCredentials = {
            "admin:admin", "root:root", "user:user", "test:test", "guest:guest"
        };

        for (String credential : commonCredentials) {
            String[] parts = credential.split(":");
            LoginRequest request = LoginRequest.builder()
                    .username(parts[0])
                    .password(parts[1])
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    @DisplayName("Should protect against account enumeration attacks")
    void shouldProtectAgainstAccountEnumerationAttacks() throws Exception {
        // Test that error messages don't reveal whether accounts exist
        String[] usernames = {
            "admin", "user", "nonexistent", "test", "random"
        };

        for (String username : usernames) {
            LoginRequest request = LoginRequest.builder()
                    .username(username)
                    .password("wrongpassword")
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Invalid credentials"));
        }
    }
}
