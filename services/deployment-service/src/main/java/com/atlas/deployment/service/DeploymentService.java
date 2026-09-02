package com.atlas.deployment.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atlas.deployment.dto.CreateDeploymentRequest;
import com.atlas.deployment.dto.DeploymentResponse;
import com.atlas.deployment.entity.Deployment;
import com.atlas.deployment.entity.DeploymentStatus;
import com.atlas.deployment.exception.DeploymentNotFoundException;
import com.atlas.deployment.repository.DeploymentRepository;
import com.atlas.events.DeploymentRequestedEvent;
import com.atlas.events.ReviewCompletedEvent;

@Service
public class DeploymentService {
    private final DeploymentRepository deploymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DeploymentService(
            DeploymentRepository deploymentRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.deploymentRepository = deploymentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public DeploymentResponse createDeployment(CreateDeploymentRequest request) {
        Deployment deployment = deploymentRepository.save(
                new Deployment(request.diagramId(), request.workspaceId(), request.diagramName(), request.diagramDefinition())
        );
        eventPublisher.publishEvent(new DeploymentRequestedEvent(
                UUID.randomUUID().toString(),
                deployment.getId(),
                deployment.getDiagramId(),
                deployment.getWorkspaceId(),
                deployment.getDiagramDefinition(),
                Instant.now()
        ));
        return toResponse(deployment);
    }

    @Transactional
    public void completeDeployment(ReviewCompletedEvent event) {
        Deployment deployment = getDeploymentEntity(event.deploymentId());
        if (deployment.getStatus() == DeploymentStatus.SUCCEEDED) {
            return;
        }
        deployment.setSimulationId(event.simulationId());
        deployment.setAiReviewId(event.reviewId());
        deployment.setStatus(DeploymentStatus.SUCCEEDED);
    }

    public DeploymentResponse getDeployment(Long id) {
        return toResponse(getDeploymentEntity(id));
    }

    public List<DeploymentResponse> getDeploymentsByDiagram(Long diagramId) {
        return deploymentRepository.findAllByDiagramId(diagramId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Deployment getDeploymentEntity(Long id) {
        return deploymentRepository.findById(id)
                .orElseThrow(() -> new DeploymentNotFoundException("Deployment with id " + id + " not found"));
    }

    private DeploymentResponse toResponse(Deployment deployment) {
        return new DeploymentResponse(
                deployment.getId(),
                deployment.getDiagramId(),
                deployment.getWorkspaceId(),
                deployment.getDiagramName(),
                deployment.getStatus(),
                deployment.getSimulationId(),
                deployment.getAiReviewId(),
                deployment.getCreatedAt(),
                deployment.getUpdatedAt()
        );
    }
}
