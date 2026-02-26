package com.atlas.notificationservice.kafka;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectionEvent {
    private UUID eventId;
    private String eventType; // REQUEST_SENT | REQUEST_ACCEPTED | REQUEST_REJECTED | UNCONNECTED | FOLLOWED | UNFOLLOWED
    private UUID senderId;
    private UUID receiverId;
    private LocalDateTime timestamp;
}
