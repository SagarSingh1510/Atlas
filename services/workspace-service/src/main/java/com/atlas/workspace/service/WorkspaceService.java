package com.atlas.workspace.service;

import com.atlas.workspace.client.AuthServiceClient;
import com.atlas.workspace.dto.AuthUserResponse;
import com.atlas.workspace.dto.CreateWorkspaceRequest;
import com.atlas.workspace.dto.UpdateWorkspaceRequest;
import com.atlas.workspace.dto.WorkspaceResponse;
import com.atlas.workspace.entity.Workspace;
import com.atlas.workspace.exception.WorkspaceNotFoundException;
import com.atlas.workspace.repository.WorkspaceRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final AuthServiceClient authServiceClient;

    public WorkspaceService(
            WorkspaceRepository workspaceRepository,
            AuthServiceClient authServiceClient
    ) {
        this.workspaceRepository = workspaceRepository;
        this.authServiceClient = authServiceClient;
    }

    public WorkspaceResponse createWorkspace(
            CreateWorkspaceRequest request
    ) {

        Long ownerId = getAuthenticatedUserId();

        Workspace workspace = new Workspace();

        workspace.setName(request.name());
        workspace.setOwnerId(ownerId);

        Workspace savedWorkspace = workspaceRepository.save(workspace);

        return new WorkspaceResponse(
                savedWorkspace.getId(),
                savedWorkspace.getName(),
                savedWorkspace.getOwnerId(),
                savedWorkspace.getCreatedAt(),
                savedWorkspace.getUpdatedAt()
        );
    }

    public WorkspaceResponse getWorkspace(Long workspaceId) {

        Long ownerId = getAuthenticatedUserId();

        Workspace workspace = workspaceRepository
                .findByIdAndOwnerId(workspaceId, ownerId)
                .orElseThrow(() ->
                        new WorkspaceNotFoundException(
                                "Workspace with id " + workspaceId + " not found"
                        )
                );

        return new WorkspaceResponse(
                workspace.getId(),
                workspace.getName(),
                workspace.getOwnerId(),
                workspace.getCreatedAt(),
                workspace.getUpdatedAt()
        );
    }

    public List<WorkspaceResponse> getWorkspaces() {

        Long ownerId = getAuthenticatedUserId();

        return workspaceRepository
                .findAllByOwnerId(ownerId)
                .stream()
                .map(workspace -> new WorkspaceResponse(
                        workspace.getId(),
                        workspace.getName(),
                        workspace.getOwnerId(),
                        workspace.getCreatedAt(),
                        workspace.getUpdatedAt()
                ))
                .toList();
    }

    public String getAuthenticatedUsername() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication.getName();
    }

    private Long getAuthenticatedUserId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String jwt = (String) authentication.getCredentials();

        AuthUserResponse user =
                authServiceClient.getCurrentUser(jwt);

        return user.id();
    }

    public WorkspaceResponse updateWorkspace(Long workspaceId,UpdateWorkspaceRequest request){
        Long ownerId = getAuthenticatedUserId();
        Workspace workspace= workspaceRepository.findByIdAndOwnerId(workspaceId, ownerId)
                                                .orElseThrow(()-> new WorkspaceNotFoundException("Workspace with id " + workspaceId + " not found"));

        workspace.setName(request.name());
        Workspace updatedWorkspace= workspaceRepository.save(workspace);
        return new WorkspaceResponse(
            updatedWorkspace.getId(),
            updatedWorkspace.getName(),
            updatedWorkspace.getOwnerId(),
            updatedWorkspace.getCreatedAt(),
            updatedWorkspace.getUpdatedAt()
        );
    }

    public void deleteWorkspace(Long workspaceId) {

        Long ownerId = getAuthenticatedUserId();

        Workspace workspace = workspaceRepository
                .findByIdAndOwnerId(workspaceId, ownerId)
                .orElseThrow(() ->
                        new WorkspaceNotFoundException(
                                "Workspace with id " + workspaceId + " not found"
                        )
                );

        workspaceRepository.delete(workspace);
        }
}