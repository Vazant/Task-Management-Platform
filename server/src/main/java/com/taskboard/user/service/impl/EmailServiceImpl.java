package com.taskboard.user.service.impl;

import com.taskboard.user.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implementation of EmailService.
 */
@Service
@ConditionalOnBean(JavaMailSender.class)
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Override
    public void sendPasswordResetEmail(String email, String username, String resetUrl) {
        log.debug("Sending password reset email to: {}", email);

        if (mailSender == null) {
            log.warn("Mail sender is not configured. Email will not be sent to: {}", email);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Password Reset Request");
            message.setText(String.format(
                "Hello %s,\n\n" +
                "You have requested to reset your password. " +
                "Please click the link below to reset your password:\n\n" +
                "%s\n\n" +
                "This link will expire in 1 hour.\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Taskboard Team",
                username, resetUrl
            ));

            mailSender.send(message);
            log.info("Password reset email sent successfully to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", email, e);
            // In production, you might want to throw an exception or handle this differently
        }
    }

    @Override
    public void sendWelcomeEmail(String email, String username) {
        log.debug("Sending welcome email to: {}", email);

        if (mailSender == null) {
            log.warn("Mail sender is not configured. Email will not be sent to: {}", email);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Welcome to Taskboard!");
            message.setText(String.format(
                "Hello %s,\n\n" +
                "Welcome to Taskboard! We're excited to have you on board.\n\n" +
                "You can now start creating projects and managing your tasks.\n\n" +
                "If you have any questions, feel free to reach out to our support team.\n\n" +
                "Best regards,\n" +
                "Taskboard Team",
                username
            ));

            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", email, e);
            // In production, you might want to throw an exception or handle this differently
        }
    }

    @Override
    public void sendEmailVerification(String email, String username, String verificationUrl) {
        log.debug("Sending email verification to: {}", email);

        if (mailSender == null) {
            log.warn("Mail sender is not configured. Email will not be sent to: {}", email);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Verify Your Email");
            message.setText(String.format(
                "Hello %s,\n\n" +
                "Please verify your email address by clicking the link below:\n\n" +
                "%s\n\n" +
                "This link will expire in 24 hours.\n\n" +
                "If you did not create an account, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Taskboard Team",
                username, verificationUrl
            ));

            mailSender.send(message);
            log.info("Email verification sent successfully to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send email verification to: {}", email, e);
            // In production, you might want to throw an exception or handle this differently
        }
    }
}
