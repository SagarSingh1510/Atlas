package com.atlas.simulation.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.atlas.simulation.dto.CreateSimulationRequest;
import com.atlas.simulation.dto.SimulationResponse;
import com.atlas.simulation.entity.Simulation;
import com.atlas.simulation.entity.SimulationStatus;
import com.atlas.simulation.exception.SimulationNotFoundException;
import com.atlas.simulation.repository.SimulationRepository;

@Service
public class SimulationService {
    private final SimulationRepository simulationRepository;

    public SimulationService(SimulationRepository simulationRepository) {
        this.simulationRepository = simulationRepository;
    }

    public SimulationResponse runSimulation(CreateSimulationRequest request) {
        String summary = "Simulation completed for diagram " + request.diagramId()
                + " with definition size " + request.diagramDefinition().length() + " characters.";
        Simulation simulation = simulationRepository.save(
                new Simulation(request.deploymentId(), request.diagramId(), request.workspaceId(), SimulationStatus.COMPLETED, summary)
        );
        return toResponse(simulation);
    }

    public SimulationResponse getSimulation(Long id) {
        return toResponse(simulationRepository.findById(id)
                .orElseThrow(() -> new SimulationNotFoundException("Simulation with id " + id + " not found")));
    }

    public List<SimulationResponse> getSimulationsByDeployment(Long deploymentId) {
        return simulationRepository.findAllByDeploymentId(deploymentId).stream()
                .map(this::toResponse)
                .toList();
    }

    private SimulationResponse toResponse(Simulation simulation) {
        return new SimulationResponse(
                simulation.getId(),
                simulation.getDeploymentId(),
                simulation.getDiagramId(),
                simulation.getWorkspaceId(),
                simulation.getStatus(),
                simulation.getSummary(),
                simulation.getCreatedAt()
        );
    }
}
