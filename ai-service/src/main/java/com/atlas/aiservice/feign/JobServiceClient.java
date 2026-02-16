package com.atlas.aiservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "job-service", fallback = JobServiceClientFallback.class)
public interface JobServiceClient {
    @GetMapping("/api/jobs/search")
    List<JobDto> searchJobs(@RequestParam("q") String query,
                            @RequestParam("location") String location,
                            @RequestParam("type") String type);
}
