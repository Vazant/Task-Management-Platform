package com.taskboard.user.service.impl;

import com.taskboard.user.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

/**
 * No-op implementation of EmailService when mail is not configured.
 */
@Service
@ConditionalOnMissingBean(EmailServiceImpl.class)
@Slf4j
public class NoOpEmailServiceImpl implements EmailService {

    @Override
    public void sendPasswordResetEmail(String email, String username, String resetUrl) {
        log.warn("Mail service is not configured. Password reset email would be sent to: {} with URL: {}", 
            email, resetUrl);
    }

    @Override
    public void sendWelcomeEmail(String email, String username) {
        log.warn("Mail service is not configured. Welcome email would be sent to: {}", email);
    }

    @Override
    public void sendEmailVerification(String email, String username, String verificationUrl) {
        log.warn("Mail service is not configured. Email verification would be sent to: {} with URL: {}", 
            email, verificationUrl);
    }
}

