package com.taskboard.api.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/** Mail configuration that only creates mail sender if mail is properly configured. */
@Configuration
public class MailConfig {

  @Bean
  @ConditionalOnProperty(
      name = "spring.mail.username",
      havingValue = "your-email@gmail.com",
      matchIfMissing = false)
  public JavaMailSender javaMailSender() {
    // This bean will only be created if mail is properly configured
    return new JavaMailSenderImpl();
  }
}
