package com.atlas.auth.controller;

import org.springframework.web.bind.annotation.RestController;

import com.atlas.auth.dto.HealthResponse;
import com.atlas.auth.model.HealthStatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class HealthController {
    
    @GetMapping("/api/v1/auth/health")
    public HealthResponse health() {
        return new HealthResponse(HealthStatus.UP,"auth-service");
    }
    
}
