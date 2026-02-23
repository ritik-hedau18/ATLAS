package com.atlas.feedservice.service;

import java.util.List;
import java.util.UUID;

public interface FeedService {
    List<String> getFeed(UUID userId, String cursor, int size);
    List<String> getTrendingFeed(int limit);
}
