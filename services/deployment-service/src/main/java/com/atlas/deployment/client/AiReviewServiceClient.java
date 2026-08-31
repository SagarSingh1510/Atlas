package com.atlas.deployment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.atlas.deployment.dto.AiReviewResponse;
import com.atlas.deployment.dto.CreateAiReviewRequest;

@Component
public class AiReviewServiceClient {
    private final RestClient restClient;

    public AiReviewServiceClient(RestClient.Builder builder, @Value("${ai-review-service.url}") String aiReviewServiceUrl) {
        this.restClient = builder.baseUrl(aiReviewServiceUrl).build();
    }

    public AiReviewResponse createReview(CreateAiReviewRequest request, String jwt) {
        return restClient.post()
                .uri("/api/v1/ai-reviews")
                .header("Authorization", "Bearer " + jwt)
                .body(request)
                .retrieve()
                .body(AiReviewResponse.class);
    }
}
