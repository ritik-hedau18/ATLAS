package com.atlas.auditlogservice.kafka;

import com.atlas.auditlogservice.document.AuditLog;
import com.atlas.auditlogservice.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogConsumer {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = {"user-events", "post-events", "connection-events", "job-events"}, groupId = "audit-log-service-group")
    public void consume(@Payload String message, 
                        @Header(value = "correlationId", required = false) String correlationId,
                        @Header(value = "traceId", required = false) String traceId) {
        log.info("Received event payload in audit-log-service");
        try {
            Map<String, Object> eventMap = objectMapper.readValue(message, Map.class);
            String eventType = eventMap.containsKey("eventType") && eventMap.get("eventType") != null 
                    ? eventMap.get("eventType").toString() : "GENERIC_EVENT";
            String eventId = eventMap.containsKey("eventId") && eventMap.get("eventId") != null 
                    ? eventMap.get("eventId").toString() : UUID.randomUUID().toString();
            
            String serviceName = "UNKNOWN_SERVICE";
            if (eventType.startsWith("USER_")) {
                serviceName = "USER-SERVICE";
            } else if (eventType.startsWith("POST_")) {
                serviceName = "POST-SERVICE";
            } else if (eventType.startsWith("REQUEST_") || eventType.startsWith("FOLLOW") || eventType.startsWith("UNCONNECTED")) {
                serviceName = "CONNECTION-SERVICE";
            } else if (eventType.startsWith("JOB_")) {
                serviceName = "JOB-SERVICE";
            }

            AuditLog auditLog = AuditLog.builder()
                    .id(eventId)
                    .serviceName(serviceName)
                    .eventType(eventType)
                    .description(message)
                    .timestamp(LocalDateTime.now().toString())
                    .correlationId(correlationId != null ? correlationId : "N/A")
                    .traceId(traceId != null ? traceId : "N/A")
                    .build();

            auditLogRepository.save(auditLog);
            log.info("Indexed audit log in Elasticsearch for event type: {}", eventType);
        } catch (Exception e) {
            log.error("Failed to parse and index audit log: {}", e.getMessage());
        }
    }
}
