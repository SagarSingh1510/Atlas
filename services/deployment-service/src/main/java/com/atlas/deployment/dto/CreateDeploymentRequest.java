package com.atlas.deployment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateDeploymentRequest(
        @NotNull Long diagramId,
        @NotNull Long workspaceId,
        @NotBlank String diagramName,
        @NotBlank String diagramDefinition
) {
}
