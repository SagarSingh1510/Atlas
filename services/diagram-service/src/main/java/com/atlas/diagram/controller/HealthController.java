package com.atlas.diagram.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    public record HealthResponse(String status, String service) {
    }

    @GetMapping("/api/v1/diagrams/health")
    public HealthResponse health() {
        return new HealthResponse("UP", "diagram-service");
    }
}
