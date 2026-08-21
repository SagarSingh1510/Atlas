package com.atlas.workspace.dto;

import java.time.LocalDateTime;

public record WorkspaceResponse(
        Long id,
        String name,
        Long ownerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}