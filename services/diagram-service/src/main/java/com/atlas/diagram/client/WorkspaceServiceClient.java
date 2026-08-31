package com.atlas.diagram.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WorkspaceServiceClient {
    private final RestClient restClient;

    public WorkspaceServiceClient(RestClient.Builder builder, @Value("${workspace-service.url}") String workspaceServiceUrl) {
        this.restClient = builder.baseUrl(workspaceServiceUrl).build();
    }

    public boolean workspaceExistsForUser(Long workspaceId, String jwt) {
        try {
            restClient.get()
                    .uri("/api/v1/workspaces/{id}", workspaceId)
                    .header("Authorization", "Bearer " + jwt)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
