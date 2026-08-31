package com.atlas.deployment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.atlas.deployment.dto.CreateSimulationRequest;
import com.atlas.deployment.dto.SimulationResponse;

@Component
public class SimulationServiceClient {
    private final RestClient restClient;

    public SimulationServiceClient(RestClient.Builder builder, @Value("${simulation-service.url}") String simulationServiceUrl) {
        this.restClient = builder.baseUrl(simulationServiceUrl).build();
    }

    public SimulationResponse runSimulation(CreateSimulationRequest request, String jwt) {
        return restClient.post()
                .uri("/api/v1/simulations")
                .header("Authorization", "Bearer " + jwt)
                .body(request)
                .retrieve()
                .body(SimulationResponse.class);
    }
}
