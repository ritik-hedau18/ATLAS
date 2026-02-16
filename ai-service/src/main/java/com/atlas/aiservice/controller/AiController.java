package com.atlas.aiservice.controller;

import com.atlas.aiservice.dto.ModerationRequest;
import com.atlas.aiservice.dto.ModerationResponse;
import com.atlas.aiservice.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/moderate")
    public ResponseEntity<ModerationResponse> moderate(@RequestBody ModerationRequest request) {
        return ResponseEntity.ok(aiService.moderate(request.getContent()));
    }

    @GetMapping("/recommendations/people")
    public ResponseEntity<List<UUID>> getPeopleRecommendations(
            @RequestParam(required = false) UUID userId,
            @RequestHeader(value = "X-User-Id", required = false) String headerUserId) {
        UUID uid = (userId != null) ? userId : (headerUserId != null ? UUID.fromString(headerUserId) : null);
        if (uid == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        return ResponseEntity.ok(aiService.getPeopleRecommendations(uid));
    }

    @GetMapping("/recommendations/jobs")
    public ResponseEntity<List<UUID>> getJobRecommendations(
            @RequestParam(required = false) UUID userId,
            @RequestHeader(value = "X-User-Id", required = false) String headerUserId) {
        UUID uid = (userId != null) ? userId : (headerUserId != null ? UUID.fromString(headerUserId) : null);
        if (uid == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        return ResponseEntity.ok(aiService.getJobRecommendations(uid));
    }
}
