package com.taskboard.user.service;

/**
 * Service interface for email operations.
 */
public interface EmailService {

    /**
     * Send password reset email to user.
     *
     * @param email the recipient email
     * @param username the username
     * @param resetUrl the password reset URL
     */
    void sendPasswordResetEmail(String email, String username, String resetUrl);

    /**
     * Send welcome email to new user.
     *
     * @param email the recipient email
     * @param username the username
     */
    void sendWelcomeEmail(String email, String username);

    /**
     * Send email verification.
     *
     * @param email the recipient email
     * @param username the username
     * @param verificationUrl the verification URL
     */
    void sendEmailVerification(String email, String username, String verificationUrl);
}
