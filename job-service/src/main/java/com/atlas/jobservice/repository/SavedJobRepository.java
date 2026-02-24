package com.atlas.jobservice.repository;

import com.atlas.jobservice.entity.SavedJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, UUID> {
    List<SavedJob> findByUserId(UUID userId);
    Optional<SavedJob> findByUserIdAndJobId(UUID userId, UUID jobId);
}
