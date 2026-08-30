package com.atlas.task.dto;

import com.atlas.task.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTaskRequest(

    @NotBlank(message = "Task title cannot be blank")
    @Size(min = 2, max = 150,
        message = "Task title must be between 2 and 150 characters")
    String title,

    @Size(max = 2000,
        message = "Task description cannot exceed 2000 characters")
    String description,

    TaskStatus status
) {
}