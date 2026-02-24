package com.atlas.jobservice.service;

import com.atlas.jobservice.entity.Company;
import com.atlas.jobservice.entity.Job;
import com.atlas.jobservice.entity.JobApplication;
import com.atlas.jobservice.entity.SavedJob;
import com.atlas.jobservice.feign.AiServiceClient;
import com.atlas.jobservice.kafka.JobEvent;
import com.atlas.jobservice.kafka.JobEventProducer;
import com.atlas.jobservice.repository.CompanyRepository;
import com.atlas.jobservice.repository.JobApplicationRepository;
import com.atlas.jobservice.repository.JobRepository;
import com.atlas.jobservice.repository.SavedJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final SavedJobRepository savedJobRepository;
    private final AiServiceClient aiServiceClient;
    private final JobEventProducer jobEventProducer;

    @Override
    @Transactional
    public Company createCompany(Company company) {
        Company savedCompany = companyRepository.save(company);
        log.info("Company profile created: {}", savedCompany.getId());
        return savedCompany;
    }

    @Override
    @Transactional
    public Job createJob(UUID posterId, Job job) {
        Company company = companyRepository.findById(job.getCompany().getId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        job.setCompany(company);
        job.setPostedBy(posterId);

        Job savedJob = jobRepository.save(job);
        log.info("Job listing created: {}", savedJob.getId());

        // Publish event
        jobEventProducer.sendJobEvent(JobEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("JOB_CREATED")
                .jobId(savedJob.getId())
                .title(savedJob.getTitle())
                .companyId(company.getId())
                .companyName(company.getName())
                .skillsRequired(savedJob.getSkillsRequired())
                .location(savedJob.getLocation())
                .timestamp(LocalDateTime.now())
                .build());

        return savedJob;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Job> searchJobs(String query, String location, String type) {
        log.info("Searching jobs locally for query: {}, location: {}", query, location);
        List<Job> jobs = jobRepository.findByTitleContainingIgnoreCaseOrLocationContainingIgnoreCase(query, location);
        if (type != null && !type.trim().isEmpty()) {
            return jobs.stream()
                    .filter(j -> type.equalsIgnoreCase(j.getJobType()))
                    .collect(Collectors.toList());
        }
        return jobs;
    }

    @Override
    @Transactional
    public JobApplication applyToJob(UUID applicantId, UUID jobId, String coverLetter) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        JobApplication application = JobApplication.builder()
                .jobId(jobId)
                .applicantId(applicantId)
                .coverLetter(coverLetter)
                .build();

        application = jobApplicationRepository.save(application);
        log.info("Application submitted for job {} by user {}", jobId, applicantId);

        // Publish event
        jobEventProducer.sendJobEvent(JobEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("JOB_APPLICATION_SUBMITTED")
                .jobId(jobId)
                .applicantId(applicantId)
                .applicationStatus("APPLIED")
                .timestamp(LocalDateTime.now())
                .build());

        return application;
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplication> getMyApplications(UUID applicantId) {
        return jobApplicationRepository.findByApplicantId(applicantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Job> getSavedJobs(UUID userId) {
        List<SavedJob> savedJobs = savedJobRepository.findByUserId(userId);
        return savedJobs.stream()
                .map(sj -> jobRepository.findById(sj.getJobId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SavedJob saveJob(UUID userId, UUID jobId) {
        Optional<SavedJob> existing = savedJobRepository.findByUserIdAndJobId(userId, jobId);
        if (existing.isPresent()) {
            savedJobRepository.delete(existing.get());
            log.info("Job {} removed from saved list for user {}", jobId, userId);
            return null;
        } else {
            SavedJob sj = SavedJob.builder()
                    .userId(userId)
                    .jobId(jobId)
                    .build();
            sj = savedJobRepository.save(sj);
            log.info("Job {} added to saved list for user {}", jobId, userId);
            return sj;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Job> getRecommendedJobs(UUID userId) {
        log.info("Requesting job recommendations from AI service for user {}", userId);
        List<UUID> jobIds = aiServiceClient.getJobRecommendations(userId);

        if (jobIds != null && !jobIds.isEmpty()) {
            return jobIds.stream()
                    .map(id -> jobRepository.findById(id).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        log.info("No matching job recommendations from AI, falling back to showing top active listings");
        // Fallback: Show first 10 active jobs
        return jobRepository.findAll().stream().limit(10).collect(Collectors.toList());
    }
}
