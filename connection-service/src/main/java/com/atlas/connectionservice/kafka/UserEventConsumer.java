package com.atlas.connectionservice.kafka;

import com.atlas.connectionservice.entity.UserNode;
import com.atlas.connectionservice.repository.UserNodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final UserNodeRepository userNodeRepository;

    @KafkaListener(topics = "user-events", groupId = "connection-service-group")
    public void consume(UserEvent event) {
        log.info("Received user event {} for user {}", event.getEventType(), event.getUserId());
        try {
            UserNode node = userNodeRepository.findByUserId(event.getUserId().toString())
                    .orElse(UserNode.builder().userId(event.getUserId().toString()).build());

            node.setUsername(event.getFullName());
            node.setHeadline(event.getHeadline());
            userNodeRepository.save(node);
            log.info("Saved user node in Neo4j for user {}", event.getUserId());
        } catch (Exception e) {
            log.error("Failed to process user event: {}", e.getMessage(), e);
        }
    }
}
