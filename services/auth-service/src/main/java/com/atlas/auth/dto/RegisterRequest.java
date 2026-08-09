package com.atlas.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotBlank(message="Username cannot be blank") @Size(min=3,max=25,message="UserName must be between 3 and 25 char long") String username,
    @Email(message="Enter valid Email") @NotBlank(message="Cannot be blank") String email,
    @NotBlank(message="cannot be blank")@Size(min=8,max=20,message="Password should be btw 8-20 char long") String password) {
    
}
