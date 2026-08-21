package com.atlas.workspace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateWorkspaceRequest(

        @NotBlank(message = "Workspace name cannot be blank")
        @Size(min = 2, max = 100, message = "Workspace name must be between 2 and 100 characters")
        String name
) {
}