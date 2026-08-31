package com.atlas.diagram.dto;

public record DeploymentRequest(Long diagramId, Long workspaceId, String diagramName, String diagramDefinition) {
}
