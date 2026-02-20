package com.atlas.connectionservice.controller;

import com.atlas.connectionservice.entity.ConnectionRequest;
import com.atlas.connectionservice.entity.UserNode;
import com.atlas.connectionservice.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/connections")
@RequiredArgsConstructor
public class ConnectionController {

    private final ConnectionService connectionService;

    @PostMapping("/request/{userId}")
    public ResponseEntity<ConnectionRequest> sendRequest(
            @RequestHeader("X-User-Id") String senderId,
            @PathVariable UUID userId) {
        return ResponseEntity.ok(connectionService.sendConnectionRequest(
                UUID.fromString(senderId), userId));
    }

    @PutMapping("/accept/{requestId}")
    public ResponseEntity<ConnectionRequest> acceptRequest(
            @RequestHeader("X-User-Id") String receiverId,
            @PathVariable UUID requestId) {
        return ResponseEntity.ok(connectionService.acceptConnectionRequest(
                UUID.fromString(receiverId), requestId));
    }

    @PutMapping("/reject/{requestId}")
    public ResponseEntity<ConnectionRequest> rejectRequest(
            @RequestHeader("X-User-Id") String receiverId,
            @PathVariable UUID requestId) {
        return ResponseEntity.ok(connectionService.rejectConnectionRequest(
                UUID.fromString(receiverId), requestId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> disconnect(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID userIdToDisconnect) {
        connectionService.disconnect(UUID.fromString(userId), userIdToDisconnect);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/follow/{userId}")
    public ResponseEntity<Void> follow(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID userIdToFollow) {
        connectionService.follow(UUID.fromString(userId), userIdToFollow);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<UserNode>> getMyConnections(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(connectionService.getMyConnections(
                UUID.fromString(userId)));
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<UserNode>> getSuggestions(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(connectionService.getSuggestions(
                UUID.fromString(userId)));
    }

    @GetMapping("/mutual/{userId}")
    public ResponseEntity<List<UserNode>> getMutualConnections(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID targetUserId) {
        return ResponseEntity.ok(connectionService.getMutualConnections(
                UUID.fromString(userId), targetUserId));
    }

    @GetMapping("/shortest-path/{userId}")
    public ResponseEntity<List<String>> getShortestPath(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID targetUserId) {
        return ResponseEntity.ok(connectionService.getShortestPath(
                UUID.fromString(userId), targetUserId));
    }
}
