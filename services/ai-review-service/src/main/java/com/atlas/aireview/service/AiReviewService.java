package com.atlas.aireview.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.atlas.aireview.dto.AiReviewResponse;
import com.atlas.aireview.dto.CreateAiReviewRequest;
import com.atlas.aireview.entity.AiReview;
import com.atlas.aireview.entity.AiReviewStatus;
import com.atlas.aireview.exception.AiReviewNotFoundException;
import com.atlas.aireview.repository.AiReviewRepository;

@Service
public class AiReviewService {
    private final AiReviewRepository aiReviewRepository;

    public AiReviewService(AiReviewRepository aiReviewRepository) {
        this.aiReviewRepository = aiReviewRepository;
    }

    public AiReviewResponse createReview(CreateAiReviewRequest request) {
        int definitionSize = request.diagramDefinition().length();
        int score = Math.max(60, Math.min(95, 95 - definitionSize / 500));
        String summary = "AI review placeholder completed for diagram " + request.diagramId()
                + ". Replace this deterministic reviewer when an AI provider is selected.";
        AiReview review = aiReviewRepository.save(
                new AiReview(request.deploymentId(), request.simulationId(), request.diagramId(), AiReviewStatus.COMPLETED, summary, score)
        );
        return toResponse(review);
    }

    public AiReviewResponse getReview(Long id) {
        return toResponse(aiReviewRepository.findById(id)
                .orElseThrow(() -> new AiReviewNotFoundException("AI review with id " + id + " not found")));
    }

    public List<AiReviewResponse> getReviewsByDeployment(Long deploymentId) {
        return aiReviewRepository.findAllByDeploymentId(deploymentId).stream()
                .map(this::toResponse)
                .toList();
    }

    private AiReviewResponse toResponse(AiReview review) {
        return new AiReviewResponse(
                review.getId(),
                review.getDeploymentId(),
                review.getSimulationId(),
                review.getDiagramId(),
                review.getStatus(),
                review.getSummary(),
                review.getScore(),
                review.getCreatedAt()
        );
    }
}
