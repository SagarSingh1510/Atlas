package com.atlas.aireview.messaging;

import java.time.Instant;
import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.atlas.aireview.entity.AiReview;
import com.atlas.aireview.entity.AiReviewStatus;
import com.atlas.aireview.repository.AiReviewRepository;
import com.atlas.events.AtlasTopics;
import com.atlas.events.ReviewCompletedEvent;
import com.atlas.events.SimulationCompletedEvent;

@Component
public class SimulationCompletedListener {
    private final AiReviewRepository reviewRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public SimulationCompletedListener(AiReviewRepository reviewRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.reviewRepository = reviewRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    @KafkaListener(topics = "${atlas.kafka.topics.simulation-completed}", groupId = "ai-review-service")
    public void handle(SimulationCompletedEvent event) {
        AiReview review = reviewRepository.findByDeploymentId(event.deploymentId())
                .orElseGet(() -> reviewRepository.save(createReview(event)));

        kafkaTemplate.send(
                AtlasTopics.REVIEW_COMPLETED,
                event.deploymentId().toString(),
                new ReviewCompletedEvent(
                        UUID.randomUUID().toString(),
                        event.deploymentId(),
                        event.simulationId(),
                        review.getId(),
                        review.getScore(),
                        Instant.now()
                )
        );
    }

    private AiReview createReview(SimulationCompletedEvent event) {
        int definitionSize = event.diagramDefinition().length();
        int score = Math.max(60, Math.min(95, 95 - definitionSize / 500));
        String summary = "Automated review completed for diagram " + event.diagramId()
                + ". Replace this deterministic reviewer when an AI provider is selected.";
        return new AiReview(
                event.deploymentId(),
                event.simulationId(),
                event.diagramId(),
                AiReviewStatus.COMPLETED,
                summary,
                score
        );
    }
}
