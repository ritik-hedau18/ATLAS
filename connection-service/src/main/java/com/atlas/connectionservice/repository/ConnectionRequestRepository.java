package com.atlas.connectionservice.repository;

import com.atlas.connectionservice.entity.ConnectionRequest;
import com.atlas.connectionservice.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConnectionRequestRepository extends JpaRepository<ConnectionRequest, UUID> {
    Optional<ConnectionRequest> findBySenderIdAndReceiverIdAndStatus(UUID senderId, UUID receiverId, RequestStatus status);
    List<ConnectionRequest> findByReceiverIdAndStatus(UUID receiverId, RequestStatus status);
    List<ConnectionRequest> findBySenderIdAndStatus(UUID senderId, RequestStatus status);
}
