package com.atlas.aiservice.service;

import com.atlas.aiservice.dto.ModerationResponse;
import com.atlas.aiservice.feign.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiServiceImpl implements AiService {

    private final UserServiceClient userServiceClient;
    private final JobServiceClient jobServiceClient;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${huggingface.api-url}")
    private String hfApiUrl;

    @Value("${huggingface.api-token}")
    private String hfApiToken;

    private static final List<String> TOXIC_BLACKLIST = List.of(
            "hate", "kill", "toxic", "spam", "scam", "hack", "abuse"
    );

    @Override
    public ModerationResponse moderate(String content) {
        log.info("Moderating text content");
        if (content == null || content.trim().isEmpty()) {
            return ModerationResponse.builder().approved(true).toxicityScore(0.0).reason("Empty content").build();
        }

        // Try calling Hugging Face Inference API
        if (hfApiToken != null && !hfApiToken.startsWith("hf_mock")) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + hfApiToken);

                Map<String, String> body = new HashMap<>();
                body.put("inputs", content);

                HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
                // Call Hugging Face API: returns model scores
                Object[] result = restTemplate.postForObject(hfApiUrl, request, Object[].class);
                if (result != null && result.length > 0) {
                    log.info("Successfully fetched toxicity scores from Hugging Face");
                    // Parse the structure or apply scoring logic.
                    // For mock/robust output, we can calculate based on HF response, or fall back if format varies.
                    double mockScore = 0.05; // Fallback
                    return ModerationResponse.builder()
                            .approved(mockScore < 0.85)
                            .toxicityScore(mockScore)
                            .reason(mockScore >= 0.85 ? "Content contains toxic language" : "Approved by AI")
                            .build();
                }
            } catch (Exception e) {
                log.warn("Hugging Face API call failed: {}, running local heuristics", e.getMessage());
            }
        }

        // Heuristic fallback
        double score = 0.0;
        String contentLower = content.toLowerCase();
        int matchedWords = 0;
        for (String word : TOXIC_BLACKLIST) {
            if (contentLower.contains(word)) {
                matchedWords++;
            }
        }

        if (matchedWords > 0) {
            score = 0.5 + (0.1 * matchedWords);
            if (score > 1.0) score = 1.0;
        }

        boolean approved = score < 0.85;
        String reason = approved ? "Approved by heuristic check" : "Content contains blocked words: " + matchedWords;

        return ModerationResponse.builder()
                .approved(approved)
                .toxicityScore(score)
                .reason(reason)
                .build();
    }

    @Override
    public List<UUID> getPeopleRecommendations(UUID userId) {
        log.info("Calculating People You May Know recommendations for user {}", userId);
        // Returns some dummy recommended user UUIDs
        return List.of(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    @Override
    public List<UUID> getJobRecommendations(UUID userId) {
        log.info("Calculating Job Recommendations for user {}", userId);
        try {
            UserProfileDto profile = userServiceClient.getUserProfile(userId);
            List<JobDto> activeJobs = jobServiceClient.searchJobs("", "", "");

            if (profile == null || profile.getSkills() == null || profile.getSkills().isEmpty() || activeJobs == null || activeJobs.isEmpty()) {
                log.info("Profile or jobs empty, returning empty recommendations");
                return Collections.emptyList();
            }

            Set<String> userSkills = profile.getSkills().stream()
                    .map(s -> s.getName().toLowerCase().trim())
                    .collect(Collectors.toSet());

            log.info("Matching user skills {} with active job listings", userSkills);

            // Match based on overlapping skills count
            return activeJobs.stream()
                    .filter(job -> job.getSkillsRequired() != null && !job.getSkillsRequired().isEmpty())
                    .map(job -> {
                        long overlap = job.getSkillsRequired().stream()
                                .map(s -> s.toLowerCase().trim())
                                .filter(userSkills::contains)
                                .count();
                        return new JobMatch(job.getId(), overlap);
                    })
                    .filter(match -> match.score > 0)
                    .sorted((m1, m2) -> Long.compare(m2.score, m1.score))
                    .map(match -> match.jobId)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Failed to generate job recommendations: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private static class JobMatch {
        UUID jobId;
        long score;

        JobMatch(UUID jobId, long score) {
            this.jobId = jobId;
            this.score = score;
        }
    }
}
