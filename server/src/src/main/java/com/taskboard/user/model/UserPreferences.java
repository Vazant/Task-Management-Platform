package com.taskboard.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;

/**
 * Embeddable class representing user preferences.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferences {

    @Column(name = "theme", length = 10)
    private String theme = "light";

    @Column(name = "language", length = 5)
    private String language = "en";

    @Embedded
    private NotificationSettings notifications = new NotificationSettings();
}
