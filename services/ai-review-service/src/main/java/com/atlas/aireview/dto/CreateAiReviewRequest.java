package com.atlas.aireview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAiReviewRequest(
        @NotNull Long deploymentId,
        @NotNull Long simulationId,
        @NotNull Long diagramId,
        @NotBlank String diagramDefinition
) {
}
