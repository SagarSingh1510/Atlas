package com.atlas.events;

import java.time.Instant;

public record SimulationCompletedEvent(
        String eventId,
        Long deploymentId,
        Long simulationId,
        Long diagramId,
        String diagramDefinition,
        Instant occurredAt
) {
}
