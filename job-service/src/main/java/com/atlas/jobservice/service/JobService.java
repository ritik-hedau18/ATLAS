package com.atlas.jobservice.service;

import com.atlas.jobservice.entity.Company;
import com.atlas.jobservice.entity.Job;
import com.atlas.jobservice.entity.JobApplication;
import com.atlas.jobservice.entity.SavedJob;

import java.util.List;
import java.util.UUID;

public interface JobService {
    Job createJob(UUID posterId, Job job);
    List<Job> searchJobs(String query, String location, String type);
    JobApplication applyToJob(UUID applicantId, UUID jobId, String coverLetter);
    List<JobApplication> getMyApplications(UUID applicantId);
    List<Job> getSavedJobs(UUID userId);
    SavedJob saveJob(UUID userId, UUID jobId);
    List<Job> getRecommendedJobs(UUID userId);
    Company createCompany(Company company);
}
