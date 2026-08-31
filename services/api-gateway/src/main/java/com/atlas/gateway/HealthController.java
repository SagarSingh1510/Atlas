package com.atlas.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    public record HealthResponse(String status, String service) {
    }

    @GetMapping("/api/v1/gateway/health")
    public HealthResponse health() {
        return new HealthResponse("UP", "api-gateway");
    }
}
