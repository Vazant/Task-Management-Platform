package com.taskboard.user.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration for user service components.
 */
@Configuration
public class UserServiceConfig {

    /**
     * Password encoder bean.
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Development mail sender that logs emails instead of sending them.
     * In production, this would be replaced with actual mail configuration.
     *
     * @return JavaMailSender instance
     */
    @Bean
    @ConditionalOnMissingBean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // For development, we'll use a mock configuration
        // In production, these would come from application properties
        mailSender.setHost("localhost");
        mailSender.setPort(1025); // Commonly used for development mail servers like MailHog

        return mailSender;
    }
}
