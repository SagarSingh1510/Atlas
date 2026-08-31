package com.atlas.aireview.dto;

import java.time.LocalDateTime;

import com.atlas.aireview.entity.AiReviewStatus;

public record AiReviewResponse(
        Long id,
        Long deploymentId,
        Long simulationId,
        Long diagramId,
        AiReviewStatus status,
        String summary,
        int score,
        LocalDateTime createdAt
) {
}
