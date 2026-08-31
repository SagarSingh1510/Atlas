package com.atlas.deployment.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atlas.deployment.client.AiReviewServiceClient;
import com.atlas.deployment.client.SimulationServiceClient;
import com.atlas.deployment.dto.AiReviewResponse;
import com.atlas.deployment.dto.CreateAiReviewRequest;
import com.atlas.deployment.dto.CreateDeploymentRequest;
import com.atlas.deployment.dto.CreateSimulationRequest;
import com.atlas.deployment.dto.DeploymentResponse;
import com.atlas.deployment.dto.SimulationResponse;
import com.atlas.deployment.entity.Deployment;
import com.atlas.deployment.entity.DeploymentStatus;
import com.atlas.deployment.exception.DeploymentNotFoundException;
import com.atlas.deployment.repository.DeploymentRepository;

@Service
public class DeploymentService {
    private final DeploymentRepository deploymentRepository;
    private final SimulationServiceClient simulationServiceClient;
    private final AiReviewServiceClient aiReviewServiceClient;

    public DeploymentService(
            DeploymentRepository deploymentRepository,
            SimulationServiceClient simulationServiceClient,
            AiReviewServiceClient aiReviewServiceClient
    ) {
        this.deploymentRepository = deploymentRepository;
        this.simulationServiceClient = simulationServiceClient;
        this.aiReviewServiceClient = aiReviewServiceClient;
    }

    @Transactional
    public DeploymentResponse createDeployment(CreateDeploymentRequest request) {
        Deployment deployment = deploymentRepository.save(
                new Deployment(request.diagramId(), request.workspaceId(), request.diagramName(), request.diagramDefinition())
        );

        try {
            SimulationResponse simulation = simulationServiceClient.runSimulation(
                    new CreateSimulationRequest(
                            deployment.getId(),
                            deployment.getDiagramId(),
                            deployment.getWorkspaceId(),
                            deployment.getDiagramDefinition()
                    ),
                    getJwt()
            );
            AiReviewResponse review = aiReviewServiceClient.createReview(
                    new CreateAiReviewRequest(
                            deployment.getId(),
                            simulation.id(),
                            deployment.getDiagramId(),
                            deployment.getDiagramDefinition()
                    ),
                    getJwt()
            );
            deployment.setSimulationId(simulation.id());
            deployment.setAiReviewId(review.id());
            deployment.setStatus(DeploymentStatus.SUCCEEDED);
        } catch (Exception e) {
            deployment.setStatus(DeploymentStatus.FAILED);
        }

        return toResponse(deploymentRepository.save(deployment));
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

    private String getJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (String) authentication.getCredentials();
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
