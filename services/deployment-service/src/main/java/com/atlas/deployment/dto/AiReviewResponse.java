package com.atlas.deployment.dto;

public record AiReviewResponse(Long id, Long deploymentId, String status, String summary) {
}
