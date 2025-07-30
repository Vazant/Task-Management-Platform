package com.taskboard.user.dto;

import lombok.*;

/**
 * DTO for simple message responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {

    private String message;

    /**
     * Static factory method for creating a message response.
     *
     * @param message the message
     * @return MessageResponse instance
     */
    public static MessageResponse of(String message) {
        return MessageResponse.builder()
                .message(message)
                .build();
    }
}
