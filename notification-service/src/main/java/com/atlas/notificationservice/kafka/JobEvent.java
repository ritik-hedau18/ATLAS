package com.atlas.notificationservice.kafka;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobEvent {
    private UUID eventId;
    private String eventType; // JOB_CREATED | JOB_UPDATED | JOB_APPLICATION_SUBMITTED | JOB_APPLICATION_STATUS_CHANGED
    private UUID jobId;
    private String title;
    private UUID companyId;
    private String companyName;
    private List<String> skillsRequired;
    private String location;
    private UUID applicantId;
    private String applicationStatus;
    private LocalDateTime timestamp;
}
