package com.atlas.task.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTaskNotFound(
            TaskNotFoundException ex
    ) {
        return new ErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                "Task Not Found",
                404
        );
    }

    @ExceptionHandler(WorkspaceAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleWorkspaceAccessDenied(
            WorkspaceAccessDeniedException ex
    ) {
        return new ErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                "Workspace Access Denied",
                403
        );
    }

    public record ErrorResponse(
            LocalDateTime timestamp,
            String message,
            String error,
            int status
    ) {
    }
}