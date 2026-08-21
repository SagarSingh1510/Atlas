package com.atlas.workspace.controller;

import com.atlas.workspace.dto.CreateWorkspaceRequest;
import com.atlas.workspace.dto.UpdateWorkspaceRequest;
import com.atlas.workspace.dto.WorkspaceResponse;
import com.atlas.workspace.service.WorkspaceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkspaceResponse createWorkspace(
            @Valid @RequestBody CreateWorkspaceRequest request
    ) {
        return workspaceService.createWorkspace(request);
    }

    @GetMapping("/{id}")
    public WorkspaceResponse getWorkspace(@PathVariable Long id) {
        return workspaceService.getWorkspace(id);
    }

    @GetMapping("/me")
    public String getAuthenticatedUser() {
        return workspaceService.getAuthenticatedUsername();
    }

    @GetMapping
    public List<WorkspaceResponse> getWorkspaces() {
        return workspaceService.getWorkspaces();
    }

    @PutMapping("/{id}")
    public WorkspaceResponse updateWorkspace(@PathVariable Long id, @RequestBody @Valid UpdateWorkspaceRequest request) {
        return workspaceService.updateWorkspace(id,request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWorkspace(@PathVariable Long id) {

        workspaceService.deleteWorkspace(id);
    }
}