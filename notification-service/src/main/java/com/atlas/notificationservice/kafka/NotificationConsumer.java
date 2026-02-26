package com.atlas.notificationservice.kafka;

import com.atlas.notificationservice.entity.Notification;
import com.atlas.notificationservice.repository.NotificationRepository;
import com.atlas.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @KafkaListener(topics = "connection-events", groupId = "notification-service-group")
    public void consumeConnectionEvents(ConnectionEvent event) {
        log.info("Received connection event: {}", event.getEventType());
        try {
            String recipientId = null;
            String message = "";
            String type = "CONNECTION_REQUEST";

            if ("REQUEST_SENT".equals(event.getEventType())) {
                recipientId = event.getReceiverId().toString();
                message = "You received a connection request from User " + event.getSenderId();
            } else if ("REQUEST_ACCEPTED".equals(event.getEventType())) {
                recipientId = event.getSenderId().toString();
                message = "User " + event.getReceiverId() + " accepted your connection request";
            }

            if (recipientId != null) {
                saveAndNotify(recipientId, type, event.getSenderId().toString(), message, event.getEventId().toString(), "CONNECTION");
            }
        } catch (Exception e) {
            log.error("Failed to process connection notification event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "post-events", groupId = "notification-service-group")
    public void consumePostEvents(PostEvent event) {
        log.info("Received post event: {}", event.getEventType());
        try {
            String recipientId = null;
            String message = "";
            String type = "POST_LIKE";

            if ("POST_LIKED".equals(event.getEventType())) {
                // In a production app we would lookup the post owner. Here, we notify the author or a target ID
                recipientId = event.getAuthorId().toString(); // Mocking target
                message = "Your post was liked by user " + event.getAuthorId();
            } else if ("POST_COMMENTED".equals(event.getEventType())) {
                recipientId = event.getAuthorId().toString(); // Mocking target
                message = "User " + event.getAuthorId() + " commented: " + event.getContent();
            }

            if (recipientId != null) {
                saveAndNotify(recipientId, type, event.getAuthorId().toString(), message, event.getPostId().toString(), "POST");
            }
        } catch (Exception e) {
            log.error("Failed to process post notification event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "job-events", groupId = "notification-service-group")
    public void consumeJobEvents(JobEvent event) {
        log.info("Received job event: {}", event.getEventType());
        try {
            String recipientId = null;
            String message = "";
            String type = "JOB_UPDATE";

            if ("JOB_APPLICATION_SUBMITTED".equals(event.getEventType())) {
                recipientId = event.getApplicantId().toString();
                message = "Your job application has been received for the role: " + event.getTitle();
            }

            if (recipientId != null) {
                saveAndNotify(recipientId, type, "SYSTEM", message, event.getJobId().toString(), "JOB");
            }
        } catch (Exception e) {
            log.error("Failed to process job notification event: {}", e.getMessage(), e);
        }
    }

    private void saveAndNotify(String recipientId, String type, String actorId, String message, String resourceId, String resourceType) {
        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .type(type)
                .actorId(actorId)
                .actorName("User " + actorId.substring(0, Math.min(8, actorId.length())))
                .message(message)
                .resourceId(resourceId)
                .resourceType(resourceType)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        notification = notificationRepository.save(notification);
        log.info("Notification saved in MongoDB: {}", notification.getNotificationId());

        // Send Email Alert Fallback
        String emailTo = recipientId + "@atlas-member.com";
        emailService.sendEmail(emailTo, "ATLAS Network: " + type, message);
    }
}
