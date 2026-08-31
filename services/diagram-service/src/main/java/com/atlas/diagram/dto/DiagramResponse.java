package com.atlas.diagram.dto;

import java.time.LocalDateTime;

public record DiagramResponse(
        Long id,
        Long workspaceId,
        String name,
        String definition,
        Long ownerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
