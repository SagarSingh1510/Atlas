package com.atlas.simulation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSimulationRequest(
        @NotNull Long deploymentId,
        @NotNull Long diagramId,
        @NotNull Long workspaceId,
        @NotBlank String diagramDefinition
) {
}
