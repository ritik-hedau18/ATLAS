package com.atlas.aiservice.feign;

import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.UUID;

@Component
public class UserServiceClientFallback implements UserServiceClient {
    @Override
    public UserProfileDto getUserProfile(UUID id) {
        return UserProfileDto.builder()
                .id(id)
                .fullName("Fallback User")
                .skills(Collections.emptyList())
                .build();
    }
}
