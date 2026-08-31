package com.atlas.deployment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.atlas.deployment.dto.CreateDeploymentRequest;
import com.atlas.deployment.dto.DeploymentResponse;
import com.atlas.deployment.service.DeploymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class DeploymentController {
    private final DeploymentService deploymentService;

    public DeploymentController(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    @PostMapping("/deployments")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DeploymentResponse createDeployment(@Valid @RequestBody CreateDeploymentRequest request) {
        return deploymentService.createDeployment(request);
    }

    @GetMapping("/deployments/{id}")
    public DeploymentResponse getDeployment(@PathVariable Long id) {
        return deploymentService.getDeployment(id);
    }

    @GetMapping("/diagrams/{diagramId}/deployments")
    public List<DeploymentResponse> getDeploymentsByDiagram(@PathVariable Long diagramId) {
        return deploymentService.getDeploymentsByDiagram(diagramId);
    }
}
