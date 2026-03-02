package com.atlas.userservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    private static final String TOPIC = "user-events";

    public void sendUserEvent(UserEvent event) {
        log.info("Publishing user event {} to topic {}", event.getEventType(), TOPIC);
        kafkaTemplate.send(TOPIC, event.getUserId().toString(), event);
    }
}
