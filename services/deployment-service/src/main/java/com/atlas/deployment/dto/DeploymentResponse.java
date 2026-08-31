package com.atlas.deployment.dto;

import java.time.LocalDateTime;

import com.atlas.deployment.entity.DeploymentStatus;

public record DeploymentResponse(
        Long id,
        Long diagramId,
        Long workspaceId,
        String diagramName,
        DeploymentStatus status,
        Long simulationId,
        Long aiReviewId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
