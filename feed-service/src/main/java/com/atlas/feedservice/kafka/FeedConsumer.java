package com.atlas.feedservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedConsumer {

    private final StringRedisTemplate redisTemplate;

    @KafkaListener(topics = "feed-fanout", groupId = "feed-service-group")
    public void consume(FeedEntryEvent event) {
        log.info("Received fanned-out feed entry for user {}: post {}, score {}", event.getUserId(), event.getPostId(), event.getScore());
        try {
            String key = "feed:" + event.getUserId();
            redisTemplate.opsForZSet().add(key, event.getPostId(), event.getScore());
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("Failed to write feed entry to Redis: {}", e.getMessage(), e);
        }
    }
}
