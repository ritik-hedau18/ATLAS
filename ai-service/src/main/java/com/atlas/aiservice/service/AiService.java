package com.atlas.aiservice.service;

import com.atlas.aiservice.dto.ModerationResponse;

import java.util.List;
import java.util.UUID;

public interface AiService {
    ModerationResponse moderate(String content);
    List<UUID> getPeopleRecommendations(UUID userId);
    List<UUID> getJobRecommendations(UUID userId);
}
