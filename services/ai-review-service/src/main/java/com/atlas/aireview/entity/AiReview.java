package com.atlas.aireview.entity;

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
@Table(name = "ai_reviews", uniqueConstraints = @UniqueConstraint(name = "uk_review_deployment", columnNames = "deployment_id"))
public class AiReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deployment_id", nullable = false)
    private Long deploymentId;

    @Column(nullable = false)
    private Long simulationId;

    @Column(nullable = false)
    private Long diagramId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AiReviewStatus status;

    @Column(nullable = false, columnDefinition = "text")
    private String summary;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected AiReview() {
    }

    public AiReview(Long deploymentId, Long simulationId, Long diagramId, AiReviewStatus status, String summary, int score) {
        this.deploymentId = deploymentId;
        this.simulationId = simulationId;
        this.diagramId = diagramId;
        this.status = status;
        this.summary = summary;
        this.score = score;
    }

    public Long getId() { return id; }
    public Long getDeploymentId() { return deploymentId; }
    public Long getSimulationId() { return simulationId; }
    public Long getDiagramId() { return diagramId; }
    public AiReviewStatus getStatus() { return status; }
    public String getSummary() { return summary; }
    public int getScore() { return score; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
