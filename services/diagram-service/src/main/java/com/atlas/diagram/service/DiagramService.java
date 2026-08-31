package com.atlas.diagram.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.atlas.diagram.client.AuthServiceClient;
import com.atlas.diagram.client.DeploymentServiceClient;
import com.atlas.diagram.client.WorkspaceServiceClient;
import com.atlas.diagram.dto.CreateDiagramRequest;
import com.atlas.diagram.dto.DeploymentRequest;
import com.atlas.diagram.dto.DeploymentResponse;
import com.atlas.diagram.dto.DiagramResponse;
import com.atlas.diagram.dto.UpdateDiagramRequest;
import com.atlas.diagram.entity.Diagram;
import com.atlas.diagram.exception.DiagramNotFoundException;
import com.atlas.diagram.exception.WorkspaceAccessDeniedException;
import com.atlas.diagram.repository.DiagramRepository;

@Service
public class DiagramService {
    private final DiagramRepository diagramRepository;
    private final WorkspaceServiceClient workspaceServiceClient;
    private final AuthServiceClient authServiceClient;
    private final DeploymentServiceClient deploymentServiceClient;

    public DiagramService(
            DiagramRepository diagramRepository,
            WorkspaceServiceClient workspaceServiceClient,
            AuthServiceClient authServiceClient,
            DeploymentServiceClient deploymentServiceClient
    ) {
        this.diagramRepository = diagramRepository;
        this.workspaceServiceClient = workspaceServiceClient;
        this.authServiceClient = authServiceClient;
        this.deploymentServiceClient = deploymentServiceClient;
    }

    public DiagramResponse createDiagram(Long workspaceId, CreateDiagramRequest request) {
        String jwt = getJwt();
        verifyWorkspaceOwnership(workspaceId, jwt);
        Long ownerId = authServiceClient.getCurrentUser(jwt).id();
        Diagram savedDiagram = diagramRepository.save(
                new Diagram(workspaceId, request.name(), request.definition(), ownerId)
        );
        return toResponse(savedDiagram);
    }

    public List<DiagramResponse> getDiagrams(Long workspaceId) {
        String jwt = getJwt();
        verifyWorkspaceOwnership(workspaceId, jwt);
        Long ownerId = authServiceClient.getCurrentUser(jwt).id();
        return diagramRepository.findAllByWorkspaceIdAndOwnerId(workspaceId, ownerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DiagramResponse getDiagram(Long id) {
        return toResponse(getAuthorizedDiagram(id));
    }

    public DiagramResponse updateDiagram(Long id, UpdateDiagramRequest request) {
        Diagram diagram = getAuthorizedDiagram(id);
        diagram.setName(request.name());
        diagram.setDefinition(request.definition());
        return toResponse(diagramRepository.save(diagram));
    }

    public void deleteDiagram(Long id) {
        diagramRepository.delete(getAuthorizedDiagram(id));
    }

    public DeploymentResponse deployDiagram(Long id) {
        Diagram diagram = getAuthorizedDiagram(id);
        return deploymentServiceClient.deploy(
                new DeploymentRequest(
                        diagram.getId(),
                        diagram.getWorkspaceId(),
                        diagram.getName(),
                        diagram.getDefinition()
                ),
                getJwt()
        );
    }

    private Diagram getAuthorizedDiagram(Long id) {
        String jwt = getJwt();
        Long ownerId = authServiceClient.getCurrentUser(jwt).id();
        return diagramRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new DiagramNotFoundException("Diagram with id " + id + " not found"));
    }

    private void verifyWorkspaceOwnership(Long workspaceId, String jwt) {
        if (!workspaceServiceClient.workspaceExistsForUser(workspaceId, jwt)) {
            throw new WorkspaceAccessDeniedException("Workspace not found or not accessible");
        }
    }

    private String getJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (String) authentication.getCredentials();
    }

    private DiagramResponse toResponse(Diagram diagram) {
        return new DiagramResponse(
                diagram.getId(),
                diagram.getWorkspaceId(),
                diagram.getName(),
                diagram.getDefinition(),
                diagram.getOwnerId(),
                diagram.getCreatedAt(),
                diagram.getUpdatedAt()
        );
    }
}
