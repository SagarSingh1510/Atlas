package com.atlas.deployment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atlas.deployment.entity.Deployment;

public interface DeploymentRepository extends JpaRepository<Deployment, Long> {
    List<Deployment> findAllByDiagramId(Long diagramId);
}
