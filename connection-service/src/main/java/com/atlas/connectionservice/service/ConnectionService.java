package com.atlas.connectionservice.service;

import com.atlas.connectionservice.entity.ConnectionRequest;
import com.atlas.connectionservice.entity.UserNode;

import java.util.List;
import java.util.UUID;

public interface ConnectionService {
    ConnectionRequest sendConnectionRequest(UUID senderId, UUID receiverId);
    ConnectionRequest acceptConnectionRequest(UUID receiverId, UUID requestId);
    ConnectionRequest rejectConnectionRequest(UUID receiverId, UUID requestId);
    void disconnect(UUID userId, UUID targetUserId);
    void follow(UUID userId, UUID targetUserId);
    List<UserNode> getMyConnections(UUID userId);
    List<UserNode> getSuggestions(UUID userId);
    List<UserNode> getMutualConnections(UUID userId1, UUID userId2);
    List<String> getShortestPath(UUID userId1, UUID userId2);
}
