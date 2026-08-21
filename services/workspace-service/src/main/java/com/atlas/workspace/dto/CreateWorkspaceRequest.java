package com.atlas.workspace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWorkspaceRequest(

        @NotBlank(message = "Workspace name is required")
        @Size(min = 3, max = 100, message = "Workspace name must be between 3 and 100 characters")
        String name

) {
}