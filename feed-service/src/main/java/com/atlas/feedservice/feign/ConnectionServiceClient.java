package com.atlas.feedservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.List;

@FeignClient(name = "connection-service", fallback = ConnectionServiceClientFallback.class)
public interface ConnectionServiceClient {
    @GetMapping("/api/connections/my")
    List<ConnectionUserDto> getConnections(@RequestHeader("X-User-Id") String userId);
}
