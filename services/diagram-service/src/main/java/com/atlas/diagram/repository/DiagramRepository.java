package com.atlas.diagram.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atlas.diagram.entity.Diagram;

public interface DiagramRepository extends JpaRepository<Diagram, Long> {
    List<Diagram> findAllByWorkspaceIdAndOwnerId(Long workspaceId, Long ownerId);
    Optional<Diagram> findByIdAndOwnerId(Long id, Long ownerId);
}
