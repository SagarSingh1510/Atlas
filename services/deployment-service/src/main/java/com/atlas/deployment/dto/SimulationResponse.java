package com.atlas.deployment.dto;

public record SimulationResponse(Long id, Long deploymentId, String status, String summary) {
}
