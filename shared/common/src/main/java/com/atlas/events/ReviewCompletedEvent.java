package com.atlas.events;

import java.time.Instant;

public record ReviewCompletedEvent(
        String eventId,
        Long deploymentId,
        Long simulationId,
        Long reviewId,
        int score,
        Instant occurredAt
) {
}
