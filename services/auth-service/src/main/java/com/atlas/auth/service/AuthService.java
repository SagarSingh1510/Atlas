package com.atlas.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.atlas.auth.dto.LoginRequest;
import com.atlas.auth.dto.LoginResponse;
import com.atlas.auth.dto.RegisterRequest;
import com.atlas.auth.dto.RegisterResponse;
import com.atlas.auth.entity.User;
import com.atlas.auth.exception.DuplicateUserException;
import com.atlas.auth.exception.InvalidCredentialsException;
import com.atlas.auth.mapper.UserMapper;
import com.atlas.auth.repository.UserRepository;
import com.atlas.auth.security.JwtService;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,PasswordEncoder passwordEncoder,JwtService jwtService){
        this.userRepository = userRepository;
        this.passwordEncoder=passwordEncoder;
        this.jwtService=jwtService;
    }

    public RegisterResponse register(RegisterRequest request){
        if(userRepository.existsByUsername(request.username())){
            throw new DuplicateUserException("Username already exists.");
        }
        if(userRepository.existsByEmail(request.email()))throw new DuplicateUserException("Email already exists.");
        String hashPassword = passwordEncoder.encode(request.password());
        User user=new User(request.username(),request.email(),hashPassword);
        User savedUser=userRepository.save(user);
        return UserMapper.toRegisterResponse(savedUser);
    }

    public LoginResponse login(LoginRequest request){
        User user=userRepository.findByUsername(request.username())
                .orElseThrow(()-> new InvalidCredentialsException("Invalid username"));

        if(!passwordEncoder.matches(request.password(), user.getPasswordHash())){
            throw new InvalidCredentialsException("Invalid password");
        }

        return new LoginResponse(user.getId(),user.getUsername(),jwtService.generateToken(user.getUsername()));
    }
}
