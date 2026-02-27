package com.atlas.searchservice.kafka;

import com.atlas.searchservice.document.JobDoc;
import com.atlas.searchservice.document.PostDoc;
import com.atlas.searchservice.document.UserDoc;
import com.atlas.searchservice.repository.JobDocRepository;
import com.atlas.searchservice.repository.PostDocRepository;
import com.atlas.searchservice.repository.UserDocRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchEventConsumer {

    private final UserDocRepository userDocRepository;
    private final PostDocRepository postDocRepository;
    private final JobDocRepository jobDocRepository;

    @KafkaListener(topics = "user-events", groupId = "search-service-group")
    public void consumeUserEvents(UserEvent event) {
        log.info("Received User event {} for user {}", event.getEventType(), event.getUserId());
        try {
            if ("USER_CREATED".equals(event.getEventType()) || "USER_UPDATED".equals(event.getEventType())) {
                UserDoc doc = UserDoc.builder()
                        .id(event.getUserId().toString())
                        .fullName(event.getFullName())
                        .headline(event.getHeadline())
                        .bio(event.getBio())
                        .location(event.getLocation())
                        .build();
                userDocRepository.save(doc);
                log.info("Indexed user doc in Elasticsearch: {}", event.getUserId());
            }
        } catch (Exception e) {
            log.error("Failed to index user doc: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "post-events", groupId = "search-service-group")
    public void consumePostEvents(PostEvent event) {
        log.info("Received Post event {} for post {}", event.getEventType(), event.getPostId());
        try {
            if ("POST_CREATED".equals(event.getEventType()) || "POST_UPDATED".equals(event.getEventType())) {
                PostDoc doc = PostDoc.builder()
                        .id(event.getPostId().toString())
                        .authorId(event.getAuthorId().toString())
                        .content(event.getContent())
                        .createdAt(event.getTimestamp() != null ? event.getTimestamp().toString() : null)
                        .build();
                postDocRepository.save(doc);
                log.info("Indexed post doc in Elasticsearch: {}", event.getPostId());
            } else if ("POST_DELETED".equals(event.getEventType())) {
                postDocRepository.deleteById(event.getPostId().toString());
                log.info("Deleted post doc from Elasticsearch: {}", event.getPostId());
            }
        } catch (Exception e) {
            log.error("Failed to index/delete post doc: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "job-events", groupId = "search-service-group")
    public void consumeJobEvents(JobEvent event) {
        log.info("Received Job event {} for job {}", event.getEventType(), event.getJobId());
        try {
            if ("JOB_CREATED".equals(event.getEventType()) || "JOB_UPDATED".equals(event.getEventType())) {
                JobDoc doc = JobDoc.builder()
                        .id(event.getJobId().toString())
                        .title(event.getTitle())
                        .description(event.getLocation()) // mapping description or other metadata
                        .skillsRequired(event.getSkillsRequired())
                        .location(event.getLocation())
                        .companyName(event.getCompanyName())
                        .build();
                jobDocRepository.save(doc);
                log.info("Indexed job doc in Elasticsearch: {}", event.getJobId());
            }
        } catch (Exception e) {
            log.error("Failed to index job doc: {}", e.getMessage(), e);
        }
    }
}
