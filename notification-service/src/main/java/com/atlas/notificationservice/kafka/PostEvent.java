package com.atlas.notificationservice.kafka;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostEvent {
    private UUID eventId;
    private String eventType; // POST_CREATED | POST_LIKED | POST_COMMENTED | POST_SHARED
    private UUID authorId;
    private UUID postId;
    private String content;
    private String visibility;
    private LocalDateTime timestamp;
}
