package com.taskboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public final class TaskboardApiApplication {

    private TaskboardApiApplication() {
        // Utility class constructor
    }

    public static void main(final String[] args) {
        SpringApplication.run(TaskboardApiApplication.class, args);
    }

}

