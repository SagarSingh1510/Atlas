package com.atlas.diagram.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.atlas.diagram.dto.CreateDiagramRequest;
import com.atlas.diagram.dto.DeploymentResponse;
import com.atlas.diagram.dto.DiagramResponse;
import com.atlas.diagram.dto.UpdateDiagramRequest;
import com.atlas.diagram.service.DiagramService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class DiagramController {
    private final DiagramService diagramService;

    public DiagramController(DiagramService diagramService) {
        this.diagramService = diagramService;
    }

    @PostMapping("/workspaces/{workspaceId}/diagrams")
    @ResponseStatus(HttpStatus.CREATED)
    public DiagramResponse createDiagram(@PathVariable Long workspaceId, @Valid @RequestBody CreateDiagramRequest request) {
        return diagramService.createDiagram(workspaceId, request);
    }

    @GetMapping("/workspaces/{workspaceId}/diagrams")
    public List<DiagramResponse> getDiagrams(@PathVariable Long workspaceId) {
        return diagramService.getDiagrams(workspaceId);
    }

    @GetMapping("/diagrams/{id}")
    public DiagramResponse getDiagram(@PathVariable Long id) {
        return diagramService.getDiagram(id);
    }

    @PutMapping("/diagrams/{id}")
    public DiagramResponse updateDiagram(@PathVariable Long id, @Valid @RequestBody UpdateDiagramRequest request) {
        return diagramService.updateDiagram(id, request);
    }

    @DeleteMapping("/diagrams/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDiagram(@PathVariable Long id) {
        diagramService.deleteDiagram(id);
    }

    @PostMapping("/diagrams/{id}/deploy")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DeploymentResponse deployDiagram(@PathVariable Long id) {
        return diagramService.deployDiagram(id);
    }
}
