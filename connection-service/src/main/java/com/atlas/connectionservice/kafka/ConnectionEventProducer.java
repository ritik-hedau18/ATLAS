package com.atlas.connectionservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionEventProducer {

    private final KafkaTemplate<String, ConnectionEvent> kafkaTemplate;
    private static final String TOPIC = "connection-events";

    public void sendConnectionEvent(ConnectionEvent event) {
        log.info("Publishing connection event {} from {} to {}", event.getEventType(), event.getSenderId(), event.getReceiverId());
        kafkaTemplate.send(TOPIC, event.getSenderId().toString(), event);
    }
}
