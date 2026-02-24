package com.atlas.jobservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobEventProducer {

    private final KafkaTemplate<String, JobEvent> kafkaTemplate;
    private static final String TOPIC = "job-events";

    public void sendJobEvent(JobEvent event) {
        log.info("Publishing job event {} for job {} to topic {}", event.getEventType(), event.getJobId(), TOPIC);
        kafkaTemplate.send(TOPIC, event.getJobId().toString(), event);
    }
}
