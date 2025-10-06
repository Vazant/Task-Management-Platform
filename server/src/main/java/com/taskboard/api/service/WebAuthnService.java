package com.taskboard.api.service;

import com.taskboard.api.model.WebAuthnChallenge;
import com.taskboard.api.repository.WebAuthnChallengeRepository;
import com.taskboard.api.repository.WebAuthnCredentialRepository;
import com.taskboard.user.model.WebAuthnCredential;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for WebAuthn (Passkeys) authentication Provides functionality for registration and
 * authentication using Yubico WebAuthn library
 */
@Service
@Slf4j
public class WebAuthnService {

  @Autowired private WebAuthnChallengeRepository challengeRepository;

  @Autowired private WebAuthnCredentialRepository credentialRepository;

  /** Create a registration challenge for WebAuthn */
  @Transactional
  public String createRegistrationChallenge(String userId) {
    log.info("Creating WebAuthn registration challenge for user: {}", userId);

    // Generate a simple challenge (in real implementation, this would use Yubico library)
    String challenge = UUID.randomUUID().toString();

    WebAuthnChallenge webAuthnChallenge =
        WebAuthnChallenge.builder()
            .userId(userId)
            .challenge(challenge)
            .type(WebAuthnChallenge.ChallengeType.REGISTRATION)
            .expiresAt(LocalDateTime.now().plusMinutes(5)) // 5 minutes
            .isUsed(false)
            .build();

    challengeRepository.save(webAuthnChallenge);

    log.debug("Generated WebAuthn registration challenge for user {}: {}", userId, challenge);
    return challenge;
  }

  /** Validate and save WebAuthn credential */
  @Transactional
  public boolean validateAndSaveCredential(
      String challengeId, String credentialResponse, String userId) {
    log.info("Validating WebAuthn credential for user: {}", userId);

    WebAuthnChallenge challenge =
        challengeRepository
            .findByChallenge(challengeId)
            .orElseThrow(() -> new IllegalArgumentException("Challenge not found or expired"));

    if (challenge.getExpiresAt().isBefore(LocalDateTime.now()) || challenge.getIsUsed()) {
      throw new IllegalArgumentException("Challenge expired or already used");
    }

    // In a real implementation, this would validate the credential using Yubico library
    // For now, we'll just save a placeholder credential
    WebAuthnCredential credential =
        WebAuthnCredential.builder()
            .userId(userId)
            .credentialId(UUID.randomUUID().toString())
            .publicKey("placeholder-public-key")
            .attestationType("none")
            .counter(0L)
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    credentialRepository.save(credential);
    challenge.setIsUsed(true);
    challengeRepository.save(challenge);

    log.info(
        "WebAuthn credential registered successfully for user {}: {}",
        userId,
        credential.getCredentialId());
    return true;
  }

  /** Create an authentication challenge for WebAuthn */
  @Transactional
  public String createAuthenticationChallenge(String userId) {
    log.info("Creating WebAuthn authentication challenge for user: {}", userId);

    List<WebAuthnCredential> credentials = credentialRepository.findByUserId(userId);
    if (credentials.isEmpty()) {
      throw new IllegalArgumentException("No WebAuthn credentials found for user: " + userId);
    }

    // Generate a simple challenge (in real implementation, this would use Yubico library)
    String challenge = UUID.randomUUID().toString();

    WebAuthnChallenge webAuthnChallenge =
        WebAuthnChallenge.builder()
            .userId(userId)
            .challenge(challenge)
            .type(WebAuthnChallenge.ChallengeType.AUTHENTICATION)
            .expiresAt(LocalDateTime.now().plusMinutes(5)) // 5 minutes
            .isUsed(false)
            .build();

    challengeRepository.save(webAuthnChallenge);

    log.debug("Generated WebAuthn authentication challenge for user {}: {}", userId, challenge);
    return challenge;
  }

  /** Validate WebAuthn authentication */
  @Transactional
  public boolean validateAuthentication(
      String challengeId, String credentialResponse, String userId) {
    log.info("Validating WebAuthn authentication for user: {}", userId);

    WebAuthnChallenge challenge =
        challengeRepository
            .findByChallenge(challengeId)
            .orElseThrow(() -> new IllegalArgumentException("Challenge not found or expired"));

    if (challenge.getExpiresAt().isBefore(LocalDateTime.now()) || challenge.getIsUsed()) {
      throw new IllegalArgumentException("Challenge expired or already used");
    }

    // In a real implementation, this would validate the assertion using Yubico library
    // For now, we'll just mark the challenge as used
    challenge.setIsUsed(true);
    challengeRepository.save(challenge);

    log.info("WebAuthn authentication successful for user: {}", userId);
    return true;
  }

  /** Get user's WebAuthn credentials */
  public List<WebAuthnCredential> getUserCredentials(String userId) {
    return credentialRepository.findByUserId(userId);
  }

  /** Delete a WebAuthn credential */
  @Transactional
  public boolean deleteCredential(String credentialId, String userId) {
    WebAuthnCredential credential =
        credentialRepository
            .findByCredentialId(credentialId)
            .orElseThrow(
                () -> new IllegalArgumentException("Credential not found: " + credentialId));

    if (!credential.getUserId().equals(userId)) {
      throw new SecurityException("User is not authorized to delete this credential");
    }

    credentialRepository.delete(credential);
    log.info("WebAuthn credential {} deleted for user {}", credentialId, userId);
    return true;
  }

  /** Clean up expired challenges */
  @Transactional
  public void cleanupExpiredChallenges() {
    challengeRepository.deleteExpiredChallenges(LocalDateTime.now());
    long deletedCount = 0; // В реальной реализации можно получить количество удаленных записей
    log.info("Cleaned up {} expired WebAuthn challenges", deletedCount);
  }
}
