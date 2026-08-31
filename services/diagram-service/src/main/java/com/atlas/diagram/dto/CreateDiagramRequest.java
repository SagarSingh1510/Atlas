package com.atlas.diagram.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateDiagramRequest(
        @NotBlank(message = "Diagram name cannot be blank") String name,
        @NotBlank(message = "Diagram definition cannot be blank") String definition
) {
}
