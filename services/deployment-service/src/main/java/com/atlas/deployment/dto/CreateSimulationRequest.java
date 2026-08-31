package com.atlas.deployment.dto;

public record CreateSimulationRequest(Long deploymentId, Long diagramId, Long workspaceId, String diagramDefinition) {
}
