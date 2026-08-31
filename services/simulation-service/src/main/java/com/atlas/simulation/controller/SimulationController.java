package com.atlas.simulation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.atlas.simulation.dto.CreateSimulationRequest;
import com.atlas.simulation.dto.SimulationResponse;
import com.atlas.simulation.service.SimulationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class SimulationController {
    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/simulations")
    @ResponseStatus(HttpStatus.CREATED)
    public SimulationResponse runSimulation(@Valid @RequestBody CreateSimulationRequest request) {
        return simulationService.runSimulation(request);
    }

    @GetMapping("/simulations/{id}")
    public SimulationResponse getSimulation(@PathVariable Long id) {
        return simulationService.getSimulation(id);
    }

    @GetMapping("/deployments/{deploymentId}/simulations")
    public List<SimulationResponse> getSimulationsByDeployment(@PathVariable Long deploymentId) {
        return simulationService.getSimulationsByDeployment(deploymentId);
    }
}
