package com.atlas.task.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atlas.task.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByWorkspaceId(Long workspaceId);
}