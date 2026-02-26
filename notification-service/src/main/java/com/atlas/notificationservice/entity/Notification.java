package com.atlas.notificationservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

import java.time.LocalDateTime;

@Document(collection = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    private String notificationId;
    private String recipientId;
    private String type; // CONNECTION_REQUEST | POST_LIKE | JOB_UPDATE | PROFILE_VIEW
    private String actorId;
    private String actorName;
    private String message;
    private String resourceId;
    private String resourceType; // POST | JOB | CONNECTION
    private boolean read;
    private LocalDateTime createdAt;
}
