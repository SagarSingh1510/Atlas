package com.atlas.aireview.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.atlas.aireview.dto.AiReviewResponse;
import com.atlas.aireview.dto.CreateAiReviewRequest;
import com.atlas.aireview.service.AiReviewService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AiReviewController {
    private final AiReviewService aiReviewService;

    public AiReviewController(AiReviewService aiReviewService) {
        this.aiReviewService = aiReviewService;
    }

    @PostMapping("/ai-reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public AiReviewResponse createReview(@Valid @RequestBody CreateAiReviewRequest request) {
        return aiReviewService.createReview(request);
    }

    @GetMapping("/ai-reviews/{id}")
    public AiReviewResponse getReview(@PathVariable Long id) {
        return aiReviewService.getReview(id);
    }

    @GetMapping("/deployments/{deploymentId}/ai-reviews")
    public List<AiReviewResponse> getReviewsByDeployment(@PathVariable Long deploymentId) {
        return aiReviewService.getReviewsByDeployment(deploymentId);
    }
}
