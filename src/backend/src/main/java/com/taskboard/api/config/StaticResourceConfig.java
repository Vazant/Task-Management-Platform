package com.taskboard.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${app.user.avatar-storage}")
    private String avatarStoragePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Раздача аватаров пользователей
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:" + avatarStoragePath + "/");

        // Раздача статических изображений (включая дефолтный аватар)
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }
}
