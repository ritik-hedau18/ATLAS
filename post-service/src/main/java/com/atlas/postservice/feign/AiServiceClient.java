package com.atlas.postservice.feign;

import com.atlas.postservice.dto.ModerationRequest;
import com.atlas.postservice.dto.ModerationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ai-service", fallback = AiServiceClientFallback.class)
public interface AiServiceClient {
    @PostMapping("/api/ai/moderate")
    ModerationResponse moderate(@RequestBody ModerationRequest request);
}
