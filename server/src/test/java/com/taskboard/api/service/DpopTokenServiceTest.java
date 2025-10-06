package com.taskboard.api.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for DPoP Token Service Tests DPoP proof token creation and validation functionality
 */
@ExtendWith(MockitoExtension.class)
class DpopTokenServiceTest {

  @InjectMocks private DpopTokenService dpopTokenService;

  private final String testSecret = "test-secret-key-for-dpop-token-service-testing-purposes-only";
  private final Long testExpiration = 900000L; // 15 minutes

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(dpopTokenService, "secret", testSecret);
    ReflectionTestUtils.setField(dpopTokenService, "expiration", testExpiration);
  }

  @Test
  void testCreateDpopProof() {
    // Given
    String accessToken = "test-access-token";
    String httpMethod = "GET";
    String httpUrl = "https://api.example.com/resource";
    String nonce = "test-nonce";

    // When
    String dpopProof = dpopTokenService.createDpopProof(accessToken, httpMethod, httpUrl, nonce);

    // Then
    assertNotNull(dpopProof);
    assertFalse(dpopProof.isEmpty());
    assertTrue(dpopProof.contains(".")); // JWT format
  }

  @Test
  void testCreateDpopProofWithoutNonce() {
    // Given
    String accessToken = "test-access-token";
    String httpMethod = "POST";
    String httpUrl = "https://api.example.com/resource";

    // When
    String dpopProof = dpopTokenService.createDpopProof(accessToken, httpMethod, httpUrl, null);

    // Then
    assertNotNull(dpopProof);
    assertFalse(dpopProof.isEmpty());
    assertTrue(dpopProof.contains(".")); // JWT format
  }

  @Test
  void testValidateDpopProofSuccess() {
    // Given
    String accessToken = "test-access-token";
    String httpMethod = "GET";
    String httpUrl = "https://api.example.com/resource";
    String nonce = "test-nonce";

    String dpopProof = dpopTokenService.createDpopProof(accessToken, httpMethod, httpUrl, nonce);

    // When
    boolean isValid =
        dpopTokenService.validateDpopProof(dpopProof, accessToken, httpMethod, httpUrl, nonce);

    // Then
    assertTrue(isValid);
  }

  @Test
  void testValidateDpopProofWithWrongAccessToken() {
    // Given
    String accessToken = "test-access-token";
    String wrongAccessToken = "wrong-access-token";
    String httpMethod = "GET";
    String httpUrl = "https://api.example.com/resource";
    String nonce = "test-nonce";

    String dpopProof = dpopTokenService.createDpopProof(accessToken, httpMethod, httpUrl, nonce);

    // When
    boolean isValid =
        dpopTokenService.validateDpopProof(dpopProof, wrongAccessToken, httpMethod, httpUrl, nonce);

    // Then
    assertFalse(isValid);
  }

  @Test
  void testValidateDpopProofWithWrongHttpMethod() {
    // Given
    String accessToken = "test-access-token";
    String httpMethod = "GET";
    String wrongHttpMethod = "POST";
    String httpUrl = "https://api.example.com/resource";
    String nonce = "test-nonce";

    String dpopProof = dpopTokenService.createDpopProof(accessToken, httpMethod, httpUrl, nonce);

    // When
    boolean isValid =
        dpopTokenService.validateDpopProof(dpopProof, accessToken, wrongHttpMethod, httpUrl, nonce);

    // Then
    assertFalse(isValid);
  }

  @Test
  void testValidateDpopProofWithWrongHttpUrl() {
    // Given
    String accessToken = "test-access-token";
    String httpMethod = "GET";
    String httpUrl = "https://api.example.com/resource";
    String wrongHttpUrl = "https://api.example.com/different-resource";
    String nonce = "test-nonce";

    String dpopProof = dpopTokenService.createDpopProof(accessToken, httpMethod, httpUrl, nonce);

    // When
    boolean isValid =
        dpopTokenService.validateDpopProof(dpopProof, accessToken, httpMethod, wrongHttpUrl, nonce);

    // Then
    assertFalse(isValid);
  }

  @Test
  void testValidateDpopProofWithWrongNonce() {
    // Given
    String accessToken = "test-access-token";
    String httpMethod = "GET";
    String httpUrl = "https://api.example.com/resource";
    String nonce = "test-nonce";
    String wrongNonce = "wrong-nonce";

    String dpopProof = dpopTokenService.createDpopProof(accessToken, httpMethod, httpUrl, nonce);

    // When
    boolean isValid =
        dpopTokenService.validateDpopProof(dpopProof, accessToken, httpMethod, httpUrl, wrongNonce);

    // Then
    assertFalse(isValid);
  }

  @Test
  void testValidateDpopProofWithInvalidToken() {
    // Given
    String accessToken = "test-access-token";
    String httpMethod = "GET";
    String httpUrl = "https://api.example.com/resource";
    String nonce = "test-nonce";
    String invalidDpopProof = "invalid.jwt.token";

    // When
    boolean isValid =
        dpopTokenService.validateDpopProof(
            invalidDpopProof, accessToken, httpMethod, httpUrl, nonce);

    // Then
    assertFalse(isValid);
  }
}
