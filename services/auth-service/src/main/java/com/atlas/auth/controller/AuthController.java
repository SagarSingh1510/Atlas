package com.atlas.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atlas.auth.dto.RegisterRequest;
import com.atlas.auth.dto.RegisterResponse;
import com.atlas.auth.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService=authService;
    }

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody @Valid RegisterRequest registerRequest) {
        //TODO: process POST request
        
        return authService.register(registerRequest);
    }
    
    
}
