package com.atlas.workspace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WorkspaceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleWorkspaceNotFound(
            WorkspaceNotFoundException ex
    ) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 404,
                "error", "Workspace Not Found",
                "message", ex.getMessage()
        );
    }
}