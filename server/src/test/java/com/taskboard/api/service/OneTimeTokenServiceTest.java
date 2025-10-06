package com.taskboard.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.taskboard.api.model.OneTimeToken;
import com.taskboard.api.repository.OneTimeTokenRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for One-Time Token Service Tests one-time token creation, validation, and management
 * functionality
 */
@ExtendWith(MockitoExtension.class)
class OneTimeTokenServiceTest {

  @Mock private OneTimeTokenRepository tokenRepository;

  @InjectMocks private OneTimeTokenService oneTimeTokenService;

  private final String testUserId = "test-user-123";
  private final int testExpirationMinutes = 15;
  private final int testMaxActiveTokens = 5;
  private final int testTokenLength = 32;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(
        oneTimeTokenService, "defaultExpirationMinutes", testExpirationMinutes);
    ReflectionTestUtils.setField(
        oneTimeTokenService, "maxActiveTokensPerUser", testMaxActiveTokens);
    ReflectionTestUtils.setField(oneTimeTokenService, "tokenLength", testTokenLength);
  }

  @Test
  void testCreateLoginToken() {
    // Given
    OneTimeToken expectedToken =
        OneTimeToken.builder()
            .token("test-token")
            .userId(testUserId)
            .purpose(OneTimeToken.TokenPurpose.LOGIN)
            .expiresAt(LocalDateTime.now().plusMinutes(testExpirationMinutes))
            .build();

    when(tokenRepository.countActiveByUserIdAndPurpose(
            eq(testUserId), eq(OneTimeToken.TokenPurpose.LOGIN), any(LocalDateTime.class)))
        .thenReturn(0L);
    when(tokenRepository.save(any(OneTimeToken.class))).thenReturn(expectedToken);

    // When
    OneTimeToken result = oneTimeTokenService.createLoginToken(testUserId, testExpirationMinutes);

    // Then
    assertNotNull(result);
    assertEquals(testUserId, result.getUserId());
    assertEquals(OneTimeToken.TokenPurpose.LOGIN, result.getPurpose());
    assertFalse(result.getIsUsed());
    assertNotNull(result.getExpiresAt());
    assertTrue(result.getExpiresAt().isAfter(LocalDateTime.now()));

    verify(tokenRepository)
        .countActiveByUserIdAndPurpose(
            eq(testUserId), eq(OneTimeToken.TokenPurpose.LOGIN), any(LocalDateTime.class));
    verify(tokenRepository).save(any(OneTimeToken.class));
  }

  @Test
  void testCreatePasswordResetToken() {
    // Given
    OneTimeToken expectedToken =
        OneTimeToken.builder()
            .token("test-token")
            .userId(testUserId)
            .purpose(OneTimeToken.TokenPurpose.PASSWORD_RESET)
            .expiresAt(LocalDateTime.now().plusMinutes(testExpirationMinutes))
            .build();

    when(tokenRepository.countActiveByUserIdAndPurpose(
            eq(testUserId), eq(OneTimeToken.TokenPurpose.PASSWORD_RESET), any(LocalDateTime.class)))
        .thenReturn(0L);
    when(tokenRepository.save(any(OneTimeToken.class))).thenReturn(expectedToken);

    // When
    OneTimeToken result =
        oneTimeTokenService.createPasswordResetToken(testUserId, testExpirationMinutes);

    // Then
    assertNotNull(result);
    assertEquals(testUserId, result.getUserId());
    assertEquals(OneTimeToken.TokenPurpose.PASSWORD_RESET, result.getPurpose());
    assertFalse(result.getIsUsed());

    verify(tokenRepository)
        .countActiveByUserIdAndPurpose(
            eq(testUserId), eq(OneTimeToken.TokenPurpose.PASSWORD_RESET), any(LocalDateTime.class));
    verify(tokenRepository).save(any(OneTimeToken.class));
  }

  @Test
  void testCreateTokenExceedsMaxActiveTokens() {
    // Given
    when(tokenRepository.countActiveByUserIdAndPurpose(
            eq(testUserId), eq(OneTimeToken.TokenPurpose.LOGIN), any(LocalDateTime.class)))
        .thenReturn((long) testMaxActiveTokens);

    // When & Then
    assertThrows(
        RuntimeException.class,
        () -> {
          oneTimeTokenService.createLoginToken(testUserId, testExpirationMinutes);
        });

    verify(tokenRepository)
        .countActiveByUserIdAndPurpose(
            eq(testUserId), eq(OneTimeToken.TokenPurpose.LOGIN), any(LocalDateTime.class));
    verify(tokenRepository, never()).save(any(OneTimeToken.class));
  }

  @Test
  void testValidateAndUseTokenSuccess() {
    // Given
    String tokenValue = "valid-token";
    OneTimeToken.TokenPurpose purpose = OneTimeToken.TokenPurpose.LOGIN;

    OneTimeToken token =
        OneTimeToken.builder()
            .token(tokenValue)
            .userId(testUserId)
            .purpose(purpose)
            .expiresAt(LocalDateTime.now().plusMinutes(10))
            .isUsed(false)
            .build();

    when(tokenRepository.findActiveByToken(eq(tokenValue), any(LocalDateTime.class)))
        .thenReturn(Optional.of(token));

    // When
    boolean result = oneTimeTokenService.validateAndUseToken(tokenValue, purpose);

    // Then
    assertTrue(result);
    verify(tokenRepository).findActiveByToken(eq(tokenValue), any(LocalDateTime.class));
    verify(tokenRepository).markAsUsed(eq(tokenValue), any(LocalDateTime.class));
  }

  @Test
  void testValidateAndUseTokenNotFound() {
    // Given
    String tokenValue = "non-existent-token";
    OneTimeToken.TokenPurpose purpose = OneTimeToken.TokenPurpose.LOGIN;

    when(tokenRepository.findActiveByToken(eq(tokenValue), any(LocalDateTime.class)))
        .thenReturn(Optional.empty());

    // When
    boolean result = oneTimeTokenService.validateAndUseToken(tokenValue, purpose);

    // Then
    assertFalse(result);
    verify(tokenRepository).findActiveByToken(eq(tokenValue), any(LocalDateTime.class));
    verify(tokenRepository, never()).markAsUsed(anyString(), any(LocalDateTime.class));
  }

  @Test
  void testValidateAndUseTokenWrongPurpose() {
    // Given
    String tokenValue = "valid-token";
    OneTimeToken.TokenPurpose tokenPurpose = OneTimeToken.TokenPurpose.LOGIN;
    OneTimeToken.TokenPurpose expectedPurpose = OneTimeToken.TokenPurpose.PASSWORD_RESET;

    OneTimeToken token =
        OneTimeToken.builder()
            .token(tokenValue)
            .userId(testUserId)
            .purpose(tokenPurpose)
            .expiresAt(LocalDateTime.now().plusMinutes(10))
            .isUsed(false)
            .build();

    when(tokenRepository.findActiveByToken(eq(tokenValue), any(LocalDateTime.class)))
        .thenReturn(Optional.of(token));

    // When
    boolean result = oneTimeTokenService.validateAndUseToken(tokenValue, expectedPurpose);

    // Then
    assertFalse(result);
    verify(tokenRepository).findActiveByToken(eq(tokenValue), any(LocalDateTime.class));
    verify(tokenRepository, never()).markAsUsed(anyString(), any(LocalDateTime.class));
  }

  @Test
  void testGetTokenInfo() {
    // Given
    String tokenValue = "valid-token";
    OneTimeToken expectedToken =
        OneTimeToken.builder()
            .token(tokenValue)
            .userId(testUserId)
            .purpose(OneTimeToken.TokenPurpose.LOGIN)
            .expiresAt(LocalDateTime.now().plusMinutes(10))
            .isUsed(false)
            .build();

    when(tokenRepository.findActiveByToken(eq(tokenValue), any(LocalDateTime.class)))
        .thenReturn(Optional.of(expectedToken));

    // When
    Optional<OneTimeToken> result = oneTimeTokenService.getTokenInfo(tokenValue);

    // Then
    assertTrue(result.isPresent());
    assertEquals(expectedToken, result.get());
    verify(tokenRepository).findActiveByToken(eq(tokenValue), any(LocalDateTime.class));
  }

  @Test
  void testGetActiveTokensForUser() {
    // Given
    List<OneTimeToken> expectedTokens =
        List.of(
            OneTimeToken.builder()
                .token("token1")
                .userId(testUserId)
                .purpose(OneTimeToken.TokenPurpose.LOGIN)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .isUsed(false)
                .build(),
            OneTimeToken.builder()
                .token("token2")
                .userId(testUserId)
                .purpose(OneTimeToken.TokenPurpose.PASSWORD_RESET)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .isUsed(false)
                .build());

    when(tokenRepository.findByUserId(testUserId)).thenReturn(expectedTokens);

    // When
    List<OneTimeToken> result = oneTimeTokenService.getActiveTokensForUser(testUserId);

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(tokenRepository).findByUserId(testUserId);
  }
}
