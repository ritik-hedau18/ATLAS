package com.atlas.connectionservice.kafka;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEvent {
    private UUID eventId;
    private String eventType; // USER_CREATED | USER_UPDATED
    private UUID userId;
    private String fullName;
    private String email;
    private String headline;
    private String bio;
    private String location;
    private LocalDateTime timestamp;
}
