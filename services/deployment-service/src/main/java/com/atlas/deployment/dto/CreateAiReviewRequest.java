package com.atlas.deployment.dto;

public record CreateAiReviewRequest(Long deploymentId, Long simulationId, Long diagramId, String diagramDefinition) {
}
