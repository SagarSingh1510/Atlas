package com.atlas.simulation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atlas.simulation.entity.Simulation;

public interface SimulationRepository extends JpaRepository<Simulation, Long> {
    List<Simulation> findAllByDeploymentId(Long deploymentId);
}
