package com.atlas.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.atlas.auth.dto.RegisterRequest;
import com.atlas.auth.dto.RegisterResponse;
import com.atlas.auth.entity.User;
import com.atlas.auth.exception.DuplicateUserException;
import com.atlas.auth.mapper.UserMapper;
import com.atlas.auth.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder=passwordEncoder;
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
}
