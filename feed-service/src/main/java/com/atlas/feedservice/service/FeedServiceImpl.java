package com.atlas.feedservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedServiceImpl implements FeedService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public List<String> getFeed(UUID userId, String cursor, int size) {
        String key = "feed:" + userId;
        double maxScore = Double.MAX_VALUE;

        if (cursor != null && !cursor.trim().isEmpty()) {
            try {
                maxScore = Double.parseDouble(cursor) - 0.0001; // Subtract tiny epsilon to avoid duplicate entry
            } catch (NumberFormatException e) {
                log.warn("Invalid cursor value: {}, defaulting to max", cursor);
            }
        }

        log.info("Fetching feed for user {} with cursor score < {}, size {}", userId, maxScore, size);
        Set<String> postIds = redisTemplate.opsForZSet().reverseRangeByScore(key, 0, maxScore, 0, size);

        if (postIds == null || postIds.isEmpty()) {
            return Collections.emptyList();
        }

        return new ArrayList<>(postIds);
    }

    @Override
    public List<String> getTrendingFeed(int limit) {
        // Retrieve trending posts from Redis key 'feed:trending'
        String key = "feed:trending";
        Set<String> postIds = redisTemplate.opsForZSet().reverseRangeByScore(key, 0, Double.MAX_VALUE, 0, limit);

        if (postIds == null || postIds.isEmpty()) {
            // Return some mock trending post IDs if cache is empty
            log.info("Trending cache is empty, returning mock trending feed IDs");
            return List.of(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString()
            );
        }

        return new ArrayList<>(postIds);
    }
}
