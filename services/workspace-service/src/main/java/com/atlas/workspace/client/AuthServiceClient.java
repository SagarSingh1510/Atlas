package com.atlas.workspace.client;

import com.atlas.workspace.dto.AuthUserResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AuthServiceClient {

    private final RestClient restClient;

    public AuthServiceClient(
            RestClient.Builder builder,
            @Value("${auth-service.url}") String authServiceUrl
    ) {
        this.restClient = builder
                .baseUrl(authServiceUrl)
                .build();
    }

    public AuthUserResponse getCurrentUser(String jwt) {

        return restClient.get()
                .uri("/api/v1/users/me")
                .header("Authorization", "Bearer " + jwt)
                .retrieve()
                .body(AuthUserResponse.class);
    }
}