package com.atlas.task.controller;

import com.atlas.task.dto.CreateTaskRequest;
import com.atlas.task.dto.TaskResponse;
import com.atlas.task.dto.UpdateTaskRequest;
import com.atlas.task.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/workspaces/{workspaceId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @PathVariable Long workspaceId,
            @Valid @RequestBody CreateTaskRequest request
    ) {
        return taskService.createTask(workspaceId, request);
    }

    @GetMapping("/workspaces/{workspaceId}/tasks")
    public List<TaskResponse> getTasks(
            @PathVariable Long workspaceId
    ) {
        return taskService.getTasksByWorkspace(workspaceId);
    }

    @GetMapping("/tasks/{id}")
    public TaskResponse getTask(
            @PathVariable Long id
    ) {
        return taskService.getTaskById(id);
    }

    @PutMapping("/tasks/{id}")
    public TaskResponse updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request
    ) {
        return taskService.updateTask(id, request);
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(
            @PathVariable Long id
    ) {
        taskService.deleteTask(id);
    }
}