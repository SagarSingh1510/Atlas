package com.atlas.gateway;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "atlas.gateway")
public record GatewayProperties(
        String authServiceUrl,
        String workspaceServiceUrl,
        String taskServiceUrl,
        String diagramServiceUrl,
        String deploymentServiceUrl,
        String simulationServiceUrl,
        String aiReviewServiceUrl
) {
}
