package com.atlas.simulation.messaging;

import java.time.Instant;
import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.atlas.events.AtlasTopics;
import com.atlas.events.DeploymentRequestedEvent;
import com.atlas.events.SimulationCompletedEvent;
import com.atlas.simulation.entity.Simulation;
import com.atlas.simulation.entity.SimulationStatus;
import com.atlas.simulation.repository.SimulationRepository;

@Component
public class DeploymentRequestedListener {
    private final SimulationRepository simulationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public DeploymentRequestedListener(SimulationRepository simulationRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.simulationRepository = simulationRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    @KafkaListener(topics = "${atlas.kafka.topics.deployment-requested}", groupId = "simulation-service")
    public void handle(DeploymentRequestedEvent event) {
        Simulation simulation = simulationRepository.findByDeploymentId(event.deploymentId())
                .orElseGet(() -> simulationRepository.save(new Simulation(
                        event.deploymentId(),
                        event.diagramId(),
                        event.workspaceId(),
                        SimulationStatus.COMPLETED,
                        "Simulation completed for diagram " + event.diagramId()
                                + " with definition size " + event.diagramDefinition().length() + " characters."
                )));

        kafkaTemplate.send(
                AtlasTopics.SIMULATION_COMPLETED,
                event.deploymentId().toString(),
                new SimulationCompletedEvent(
                        UUID.randomUUID().toString(),
                        event.deploymentId(),
                        simulation.getId(),
                        event.diagramId(),
                        event.diagramDefinition(),
                        Instant.now()
                )
        );
    }
}
