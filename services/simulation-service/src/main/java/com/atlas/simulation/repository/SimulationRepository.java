package com.atlas.simulation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atlas.simulation.entity.Simulation;

public interface SimulationRepository extends JpaRepository<Simulation, Long> {
    List<Simulation> findAllByDeploymentId(Long deploymentId);
    Optional<Simulation> findByDeploymentId(Long deploymentId);
}
