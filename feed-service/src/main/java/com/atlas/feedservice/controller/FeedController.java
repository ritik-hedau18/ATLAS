package com.atlas.feedservice.controller;

import com.atlas.feedservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public ResponseEntity<List<String>> getFeed(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(feedService.getFeed(UUID.fromString(userId), cursor, size));
    }

    @GetMapping("/trending")
    public ResponseEntity<List<String>> getTrendingFeed(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(feedService.getTrendingFeed(limit));
    }
}
