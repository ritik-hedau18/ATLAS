package com.atlas.feedservice.feign;

import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Component
public class ConnectionServiceClientFallback implements ConnectionServiceClient {
    @Override
    public List<ConnectionUserDto> getConnections(String userId) {
        return Collections.emptyList();
    }
}
