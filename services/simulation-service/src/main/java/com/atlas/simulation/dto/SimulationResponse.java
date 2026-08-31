package com.atlas.simulation.dto;

import java.time.LocalDateTime;

import com.atlas.simulation.entity.SimulationStatus;

public record SimulationResponse(
        Long id,
        Long deploymentId,
        Long diagramId,
        Long workspaceId,
        SimulationStatus status,
        String summary,
        LocalDateTime createdAt
) {
}
