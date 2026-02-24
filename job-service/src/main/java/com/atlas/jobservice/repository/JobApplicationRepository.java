package com.atlas.jobservice.repository;

import com.atlas.jobservice.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {
    List<JobApplication> findByApplicantId(UUID applicantId);
    List<JobApplication> findByJobId(UUID jobId);
}
