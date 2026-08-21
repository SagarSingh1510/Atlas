package com.atlas.workspace.repository;

import com.atlas.workspace.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;


public interface WorkspaceRepository extends JpaRepository<Workspace,Long> {
    Optional<Workspace> findByIdAndOwnerId(Long id, Long ownerId);
    List<Workspace> findAllByOwnerId(Long ownerId);
}
