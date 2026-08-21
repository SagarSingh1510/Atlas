package com.atlas.workspace.dto;

public record AuthUserResponse(
        Long id,
        String username,
        String email
) {
}