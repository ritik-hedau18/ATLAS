package com.atlas.connectionservice.service;

import com.atlas.connectionservice.entity.ConnectionRequest;
import com.atlas.connectionservice.entity.RequestStatus;
import com.atlas.connectionservice.entity.UserNode;
import com.atlas.connectionservice.kafka.ConnectionEvent;
import com.atlas.connectionservice.kafka.ConnectionEventProducer;
import com.atlas.connectionservice.repository.ConnectionRequestRepository;
import com.atlas.connectionservice.repository.UserNodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionServiceImpl implements ConnectionService {

    private final UserNodeRepository userNodeRepository;
    private final ConnectionRequestRepository connectionRequestRepository;
    private final ConnectionEventProducer connectionEventProducer;

    private UserNode getOrCreateNode(UUID userId) {
        return userNodeRepository.findByUserId(userId.toString())
                .orElseGet(() -> {
                    UserNode node = UserNode.builder()
                            .userId(userId.toString())
                            .username("User " + userId.toString().substring(0, 8))
                            .headline("LinkedIn Member")
                            .build();
                    return userNodeRepository.save(node);
                });
    }

    @Override
    @Transactional
    public ConnectionRequest sendConnectionRequest(UUID senderId, UUID receiverId) {
        if (senderId.equals(receiverId)) {
            throw new RuntimeException("Cannot connect with yourself");
        }

        // Check if there is already a pending connection request
        connectionRequestRepository.findBySenderIdAndReceiverIdAndStatus(senderId, receiverId, RequestStatus.PENDING)
                .ifPresent(r -> { throw new RuntimeException("Connection request already pending"); });

        // Ensure nodes exist in Neo4j
        getOrCreateNode(senderId);
        getOrCreateNode(receiverId);

        ConnectionRequest request = ConnectionRequest.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .status(RequestStatus.PENDING)
                .build();

        request = connectionRequestRepository.save(request);
        log.info("Connection request sent: {} -> {}", senderId, receiverId);

        // Publish event to Kafka
        connectionEventProducer.sendConnectionEvent(ConnectionEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("REQUEST_SENT")
                .senderId(senderId)
                .receiverId(receiverId)
                .timestamp(LocalDateTime.now())
                .build());

        return request;
    }

    @Override
    @Transactional
    public ConnectionRequest acceptConnectionRequest(UUID receiverId, UUID requestId) {
        ConnectionRequest request = connectionRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Connection request not found"));

        if (!request.getReceiverId().equals(receiverId)) {
            throw new RuntimeException("Unauthorized action");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Connection request is already processed");
        }

        request.setStatus(RequestStatus.ACCEPTED);
        request = connectionRequestRepository.save(request);

        // Establish connection in Neo4j
        userNodeRepository.createConnection(request.getSenderId().toString(), request.getReceiverId().toString());
        userNodeRepository.createConnection(request.getReceiverId().toString(), request.getSenderId().toString());
        log.info("Connection established in Neo4j: {} <-> {}", request.getSenderId(), request.getReceiverId());

        // Publish event to Kafka
        connectionEventProducer.sendConnectionEvent(ConnectionEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("REQUEST_ACCEPTED")
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .timestamp(LocalDateTime.now())
                .build());

        return request;
    }

    @Override
    @Transactional
    public ConnectionRequest rejectConnectionRequest(UUID receiverId, UUID requestId) {
        ConnectionRequest request = connectionRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Connection request not found"));

        if (!request.getReceiverId().equals(receiverId)) {
            throw new RuntimeException("Unauthorized action");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Connection request is already processed");
        }

        request.setStatus(RequestStatus.REJECTED);
        request = connectionRequestRepository.save(request);

        // Publish event
        connectionEventProducer.sendConnectionEvent(ConnectionEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("REQUEST_REJECTED")
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .timestamp(LocalDateTime.now())
                .build());

        return request;
    }

    @Override
    @Transactional
    public void disconnect(UUID userId, UUID targetUserId) {
        userNodeRepository.deleteConnection(userId.toString(), targetUserId.toString());
        log.info("Deleted connection in Neo4j between {} and {}", userId, targetUserId);

        // Publish event
        connectionEventProducer.sendConnectionEvent(ConnectionEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("UNCONNECTED")
                .senderId(userId)
                .receiverId(targetUserId)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Override
    @Transactional
    public void follow(UUID userId, UUID targetUserId) {
        getOrCreateNode(userId);
        getOrCreateNode(targetUserId);

        userNodeRepository.createFollow(userId.toString(), targetUserId.toString());
        log.info("Follow created: {} follows {}", userId, targetUserId);

        // Publish event
        connectionEventProducer.sendConnectionEvent(ConnectionEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("FOLLOWED")
                .senderId(userId)
                .receiverId(targetUserId)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserNode> getMyConnections(UUID userId) {
        return userNodeRepository.findFirstDegreeConnections(userId.toString());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserNode> getSuggestions(UUID userId) {
        return userNodeRepository.findPeopleYouMayKnow(userId.toString());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserNode> getMutualConnections(UUID userId1, UUID userId2) {
        return userNodeRepository.findMutualConnections(userId1.toString(), userId2.toString());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getShortestPath(UUID userId1, UUID userId2) {
        return userNodeRepository.findShortestPath(userId1.toString(), userId2.toString());
    }
}
