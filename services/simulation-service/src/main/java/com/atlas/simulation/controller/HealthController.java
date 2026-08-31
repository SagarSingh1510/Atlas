package com.atlas.simulation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    public record HealthResponse(String status, String service) {
    }

    @GetMapping("/api/v1/simulations/health")
    public HealthResponse health() {
        return new HealthResponse("UP", "simulation-service");
    }
}
