package com.atlas.simulation.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "simulations", uniqueConstraints = @UniqueConstraint(name = "uk_simulation_deployment", columnNames = "deployment_id"))
public class Simulation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deployment_id", nullable = false)
    private Long deploymentId;

    @Column(nullable = false)
    private Long diagramId;

    @Column(nullable = false)
    private Long workspaceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SimulationStatus status;

    @Column(nullable = false, columnDefinition = "text")
    private String summary;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Simulation() {
    }

    public Simulation(Long deploymentId, Long diagramId, Long workspaceId, SimulationStatus status, String summary) {
        this.deploymentId = deploymentId;
        this.diagramId = diagramId;
        this.workspaceId = workspaceId;
        this.status = status;
        this.summary = summary;
    }

    public Long getId() { return id; }
    public Long getDeploymentId() { return deploymentId; }
    public Long getDiagramId() { return diagramId; }
    public Long getWorkspaceId() { return workspaceId; }
    public SimulationStatus getStatus() { return status; }
    public String getSummary() { return summary; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
