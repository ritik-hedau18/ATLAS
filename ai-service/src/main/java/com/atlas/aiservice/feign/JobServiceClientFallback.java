package com.atlas.aiservice.feign;

import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Component
public class JobServiceClientFallback implements JobServiceClient {
    @Override
    public List<JobDto> searchJobs(String query, String location, String type) {
        return Collections.emptyList();
    }
}
