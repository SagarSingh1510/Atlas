package com.atlas.events;

import java.time.Instant;

public record DeploymentRequestedEvent(
        String eventId,
        Long deploymentId,
        Long diagramId,
        Long workspaceId,
        String diagramDefinition,
        Instant occurredAt
) {
}
