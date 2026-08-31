package com.atlas.deployment.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "deployments")
public class Deployment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long diagramId;

    @Column(nullable = false)
    private Long workspaceId;

    @Column(nullable = false)
    private String diagramName;

    @Column(nullable = false, columnDefinition = "text")
    private String diagramDefinition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeploymentStatus status;

    private Long simulationId;
    private Long aiReviewId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Deployment() {
    }

    public Deployment(Long diagramId, Long workspaceId, String diagramName, String diagramDefinition) {
        this.diagramId = diagramId;
        this.workspaceId = workspaceId;
        this.diagramName = diagramName;
        this.diagramDefinition = diagramDefinition;
        this.status = DeploymentStatus.PENDING;
    }

    public Long getId() { return id; }
    public Long getDiagramId() { return diagramId; }
    public Long getWorkspaceId() { return workspaceId; }
    public String getDiagramName() { return diagramName; }
    public String getDiagramDefinition() { return diagramDefinition; }
    public DeploymentStatus getStatus() { return status; }
    public Long getSimulationId() { return simulationId; }
    public Long getAiReviewId() { return aiReviewId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setStatus(DeploymentStatus status) { this.status = status; }
    public void setSimulationId(Long simulationId) { this.simulationId = simulationId; }
    public void setAiReviewId(Long aiReviewId) { this.aiReviewId = aiReviewId; }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
