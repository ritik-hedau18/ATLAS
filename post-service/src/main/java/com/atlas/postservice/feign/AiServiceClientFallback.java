package com.atlas.postservice.feign;

import com.atlas.postservice.dto.ModerationRequest;
import com.atlas.postservice.dto.ModerationResponse;
import org.springframework.stereotype.Component;

@Component
public class AiServiceClientFallback implements AiServiceClient {
    @Override
    public ModerationResponse moderate(ModerationRequest request) {
        return ModerationResponse.builder()
                .approved(true)
                .toxicityScore(0.0)
                .reason("AI Service fallback - service unavailable")
                .build();
    }
}
