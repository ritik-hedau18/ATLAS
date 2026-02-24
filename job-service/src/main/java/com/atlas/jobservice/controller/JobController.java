package com.atlas.jobservice.controller;

import com.atlas.jobservice.entity.Company;
import com.atlas.jobservice.entity.Job;
import com.atlas.jobservice.entity.JobApplication;
import com.atlas.jobservice.entity.SavedJob;
import com.atlas.jobservice.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping("/company")
    public ResponseEntity<Company> createCompany(@RequestBody Company company) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.createCompany(company));
    }

    @PostMapping
    public ResponseEntity<Job> createJob(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody Job job) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobService.createJob(UUID.fromString(userId), job));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Job>> searchJobs(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false, defaultValue = "") String location,
            @RequestParam(required = false, defaultValue = "") String type) {
        return ResponseEntity.ok(jobService.searchJobs(q, location, type));
    }

    @PostMapping("/{id}/apply")
    public ResponseEntity<JobApplication> apply(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID id,
            @RequestBody(required = false) String coverLetter) {
        String cl = (coverLetter != null) ? coverLetter : "Default application cover letter";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobService.applyToJob(UUID.fromString(userId), id, cl));
    }

    @GetMapping("/my-applications")
    public ResponseEntity<List<JobApplication>> getMyApplications(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(jobService.getMyApplications(UUID.fromString(userId)));
    }

    @GetMapping("/saved")
    public ResponseEntity<List<Job>> getSavedJobs(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(jobService.getSavedJobs(UUID.fromString(userId)));
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<SavedJob> save(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(jobService.saveJob(UUID.fromString(userId), id));
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<Job>> getRecommended(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(jobService.getRecommendedJobs(UUID.fromString(userId)));
    }
}
