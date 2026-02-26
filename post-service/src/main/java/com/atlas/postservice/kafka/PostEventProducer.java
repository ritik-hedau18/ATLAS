package com.atlas.postservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostEventProducer {

    private final KafkaTemplate<String, PostEvent> kafkaTemplate;
    private static final String TOPIC = "post-events";

    public void sendPostEvent(PostEvent event) {
        log.info("Publishing post event {} for post {} to topic {}", event.getEventType(), event.getPostId(), TOPIC);
        kafkaTemplate.send(TOPIC, event.getPostId().toString(), event);
    }
}
