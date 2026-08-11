package com.atlas.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank(message = "Username cannot be Blank")String username,@NotBlank(message = "Password cannot be blank")String password){}
    

