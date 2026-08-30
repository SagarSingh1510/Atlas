package com.atlas.task.service;

import com.atlas.task.client.WorkspaceServiceClient;
import com.atlas.task.dto.CreateTaskRequest;
import com.atlas.task.dto.TaskResponse;
import com.atlas.task.dto.UpdateTaskRequest;
import com.atlas.task.entity.Task;
import com.atlas.task.exception.TaskNotFoundException;
import com.atlas.task.exception.WorkspaceAccessDeniedException;
import com.atlas.task.repository.TaskRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final WorkspaceServiceClient workspaceServiceClient;

    public TaskService(TaskRepository taskRepository,WorkspaceServiceClient workspaceServiceClient) {
        this.taskRepository = taskRepository;
        this.workspaceServiceClient=workspaceServiceClient;
    }
public TaskResponse createTask(
        Long workspaceId,
        CreateTaskRequest request) {
        verifyWorkspaceOwnership(workspaceId);
        Task task = new Task(
                workspaceId,
                request.title(),
                request.description()
        );

        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
    }

    public List<TaskResponse> getTasksByWorkspace(Long workspaceId) {

        verifyWorkspaceOwnership(workspaceId);

        return taskRepository.findAllByWorkspaceId(workspaceId)
                        .stream()
                        .map(this::toResponse)
                        .toList();
    }

    private Task getAuthorizedTask(Long taskId) {

        Task task = taskRepository.findById(taskId)
        .orElseThrow(() ->
                new TaskNotFoundException(
                        "Task with id " + taskId + " not found"
                )
        );

        verifyWorkspaceOwnership(task.getWorkspaceId());

        return task;
        }

        public TaskResponse getTaskById(Long id) {

                Task task = getAuthorizedTask(id);

                return toResponse(task);
        }

    public TaskResponse updateTask(
            Long id,
            UpdateTaskRequest request
    ) {

        Task task = getAuthorizedTask(id);

        task.setTitle(request.title());
        task.setDescription(request.description());

        if (request.status() != null) {
            task.setStatus(request.status());
        }

        Task updatedTask = taskRepository.save(task);

        return toResponse(updatedTask);
    }

    public void deleteTask(Long id) {

        Task task = getAuthorizedTask(id);

        taskRepository.delete(task);
    }

    private TaskResponse toResponse(Task task) {

        return new TaskResponse(
                task.getId(),
                task.getWorkspaceId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    private void verifyWorkspaceOwnership(Long workspaceId) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String jwt = (String) authentication.getCredentials();

        boolean ownsWorkspace =
                workspaceServiceClient.workspaceExistsForUser(
                        workspaceId,
                        jwt
                );

        if (!ownsWorkspace) {
                throw new WorkspaceAccessDeniedException(
                        "Workspace not found or not accessible"
                );
        }
        }
}