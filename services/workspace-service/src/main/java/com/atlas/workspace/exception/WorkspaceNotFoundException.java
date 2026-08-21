package com.atlas.workspace.exception;

public class WorkspaceNotFoundException extends RuntimeException {

    public WorkspaceNotFoundException(String message) {
        super(message);
    }
}