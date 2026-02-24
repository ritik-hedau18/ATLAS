package com.atlas.jobservice.feign;

import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class AiServiceClientFallback implements AiServiceClient {
    @Override
    public List<UUID> getJobRecommendations(UUID userId) {
        return Collections.emptyList();
    }
}
