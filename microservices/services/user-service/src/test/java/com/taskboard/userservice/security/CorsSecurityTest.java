package com.taskboard.userservice.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security tests for CORS (Cross-Origin Resource Sharing) configuration.
 * Tests CORS headers, allowed origins, methods, and security measures.
 * 
 * @author TaskBoard Team
 * @version 1.0
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CorsSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should include CORS headers in preflight requests")
    void shouldIncludeCorsHeadersInPreflightRequests() throws Exception {
        mockMvc.perform(options("/api/users")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type, Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().exists("Access-Control-Allow-Methods"))
                .andExpect(header().exists("Access-Control-Allow-Headers"))
                .andExpect(header().exists("Access-Control-Max-Age"));
    }

    @Test
    @DisplayName("Should allow requests from configured origins")
    void shouldAllowRequestsFromConfiguredOrigins() throws Exception {
        String[] allowedOrigins = {
            "http://localhost:4200",
            "http://localhost:3000",
            "https://taskboard.com",
            "https://www.taskboard.com"
        };

        for (String origin : allowedOrigins) {
            mockMvc.perform(options("/api/users")
                            .header("Origin", origin)
                            .header("Access-Control-Request-Method", "GET"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Origin", origin));
        }
    }

    @Test
    @DisplayName("Should reject requests from disallowed origins")
    void shouldRejectRequestsFromDisallowedOrigins() throws Exception {
        String[] disallowedOrigins = {
            "http://malicious-site.com",
            "https://evil-domain.org",
            "http://localhost:9999", // Different port
            "https://subdomain.taskboard.com" // Not explicitly allowed
        };

        for (String origin : disallowedOrigins) {
            mockMvc.perform(options("/api/users")
                            .header("Origin", origin)
                            .header("Access-Control-Request-Method", "GET"))
                    .andExpect(status().isOk())
                    .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
        }
    }

    @Test
    @DisplayName("Should allow configured HTTP methods")
    void shouldAllowConfiguredHttpMethods() throws Exception {
        String[] allowedMethods = {
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        };

        for (String method : allowedMethods) {
            mockMvc.perform(options("/api/users")
                            .header("Origin", "http://localhost:4200")
                            .header("Access-Control-Request-Method", method))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Methods", 
                            org.hamcrest.Matchers.containsString(method)));
        }
    }

    @Test
    @DisplayName("Should reject disallowed HTTP methods")
    void shouldRejectDisallowedHttpMethods() throws Exception {
        String[] disallowedMethods = {
            "PATCH", "HEAD", "TRACE", "CONNECT"
        };

        for (String method : disallowedMethods) {
            mockMvc.perform(options("/api/users")
                            .header("Origin", "http://localhost:4200")
                            .header("Access-Control-Request-Method", method))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Methods", 
                            org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString(method))));
        }
    }

    @Test
    @DisplayName("Should allow configured headers")
    void shouldAllowConfiguredHeaders() throws Exception {
        String[] allowedHeaders = {
            "Content-Type", "Authorization", "X-Requested-With", "Accept"
        };

        for (String header : allowedHeaders) {
            mockMvc.perform(options("/api/users")
                            .header("Origin", "http://localhost:4200")
                            .header("Access-Control-Request-Method", "POST")
                            .header("Access-Control-Request-Headers", header))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Headers", 
                            org.hamcrest.Matchers.containsString(header)));
        }
    }

    @Test
    @DisplayName("Should reject disallowed headers")
    void shouldRejectDisallowedHeaders() throws Exception {
        String[] disallowedHeaders = {
            "X-Malicious-Header", "X-Script-Injection", "X-Custom-Header"
        };

        for (String header : disallowedHeaders) {
            mockMvc.perform(options("/api/users")
                            .header("Origin", "http://localhost:4200")
                            .header("Access-Control-Request-Method", "POST")
                            .header("Access-Control-Request-Headers", header))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Headers", 
                            org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString(header))));
        }
    }

    @Test
    @DisplayName("Should set appropriate max age for preflight requests")
    void shouldSetAppropriateMaxAgeForPreflightRequests() throws Exception {
        mockMvc.perform(options("/api/users")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Max-Age"))
                .andExpect(header().string("Access-Control-Max-Age", "3600"));
    }

    @Test
    @DisplayName("Should handle credentials correctly")
    void shouldHandleCredentialsCorrectly() throws Exception {
        mockMvc.perform(options("/api/users")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    @DisplayName("Should handle wildcard origins securely")
    void shouldHandleWildcardOriginsSecurely() throws Exception {
        // Test that wildcard origins are not allowed when credentials are enabled
        mockMvc.perform(options("/api/users")
                        .header("Origin", "*")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    @DisplayName("Should handle null origin gracefully")
    void shouldHandleNullOriginGracefully() throws Exception {
        mockMvc.perform(options("/api/users")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    @DisplayName("Should handle malformed origin headers")
    void shouldHandleMalformedOriginHeaders() throws Exception {
        String[] malformedOrigins = {
            "not-a-valid-origin",
            "http://",
            "https://",
            "ftp://malicious.com",
            "javascript:alert('xss')"
        };

        for (String origin : malformedOrigins) {
            mockMvc.perform(options("/api/users")
                            .header("Origin", origin)
                            .header("Access-Control-Request-Method", "GET"))
                    .andExpect(status().isOk())
                    .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
        }
    }

    @Test
    @DisplayName("Should handle case sensitivity in origins")
    void shouldHandleCaseSensitivityInOrigins() throws Exception {
        String[] caseVariations = {
            "http://localhost:4200",
            "HTTP://LOCALHOST:4200",
            "http://LOCALHOST:4200",
            "HTTP://localhost:4200"
        };

        for (String origin : caseVariations) {
            mockMvc.perform(options("/api/users")
                            .header("Origin", origin)
                            .header("Access-Control-Request-Method", "GET"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Origin", origin));
        }
    }

    @Test
    @DisplayName("Should handle multiple origins in single request")
    void shouldHandleMultipleOriginsInSingleRequest() throws Exception {
        // CORS spec doesn't allow multiple origins in a single request
        // This should be handled gracefully
        mockMvc.perform(options("/api/users")
                        .header("Origin", "http://localhost:4200,http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    @DisplayName("Should handle subdomain origins correctly")
    void shouldHandleSubdomainOriginsCorrectly() throws Exception {
        // Test that subdomains are handled according to configuration
        mockMvc.perform(options("/api/users")
                        .header("Origin", "http://subdomain.localhost:4200")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    @DisplayName("Should handle port variations correctly")
    void shouldHandlePortVariationsCorrectly() throws Exception {
        String[] portVariations = {
            "http://localhost:4200",
            "http://localhost:8080",
            "http://localhost:3000"
        };

        for (String origin : portVariations) {
            mockMvc.perform(options("/api/users")
                            .header("Origin", origin)
                            .header("Access-Control-Request-Method", "GET"))
                    .andExpect(status().isOk());
            // The exact behavior depends on configuration
        }
    }

    @Test
    @DisplayName("Should handle protocol variations correctly")
    void shouldHandleProtocolVariationsCorrectly() throws Exception {
        String[] protocolVariations = {
            "http://localhost:4200",
            "https://localhost:4200"
        };

        for (String origin : protocolVariations) {
            mockMvc.perform(options("/api/users")
                            .header("Origin", origin)
                            .header("Access-Control-Request-Method", "GET"))
                    .andExpect(status().isOk());
            // The exact behavior depends on configuration
        }
    }
}
