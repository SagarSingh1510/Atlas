package com.atlas.diagram.dto;

public record DeploymentResponse(Long id, Long diagramId, String status, Long simulationId, Long aiReviewId) {
}
