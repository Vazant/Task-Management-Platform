package com.taskboard.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class TaskboardApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskboardApiApplication.class, args);
    }

}
