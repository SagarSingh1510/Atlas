package com.atlas.task.dto;

import com.atlas.task.entity.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponse(
    Long id,
    Long workspaceId,
    String title,
    String description,
    TaskStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}