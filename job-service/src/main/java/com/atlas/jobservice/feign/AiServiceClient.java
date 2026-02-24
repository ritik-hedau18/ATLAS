package com.atlas.jobservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "ai-service", fallback = AiServiceClientFallback.class)
public interface AiServiceClient {
    @GetMapping("/api/ai/recommendations/jobs")
    List<UUID> getJobRecommendations(@RequestParam("userId") UUID userId);
}
