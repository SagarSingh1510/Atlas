package com.atlas.deployment.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.atlas.deployment.service.DeploymentService;
import com.atlas.events.ReviewCompletedEvent;

@Component
public class ReviewCompletedListener {
    private final DeploymentService deploymentService;

    public ReviewCompletedListener(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    @KafkaListener(topics = "${atlas.kafka.topics.review-completed}", groupId = "deployment-service")
    public void handle(ReviewCompletedEvent event) {
        deploymentService.completeDeployment(event);
    }
}
