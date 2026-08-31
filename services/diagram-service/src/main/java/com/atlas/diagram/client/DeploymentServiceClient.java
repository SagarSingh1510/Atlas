package com.atlas.diagram.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.atlas.diagram.dto.DeploymentRequest;
import com.atlas.diagram.dto.DeploymentResponse;

@Component
public class DeploymentServiceClient {
    private final RestClient restClient;

    public DeploymentServiceClient(RestClient.Builder builder, @Value("${deployment-service.url}") String deploymentServiceUrl) {
        this.restClient = builder.baseUrl(deploymentServiceUrl).build();
    }

    public DeploymentResponse deploy(DeploymentRequest request, String jwt) {
        return restClient.post()
                .uri("/api/v1/deployments")
                .header("Authorization", "Bearer " + jwt)
                .body(request)
                .retrieve()
                .body(DeploymentResponse.class);
    }
}
