package com.atlas.auth.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.atlas.auth.dto.UserResponse;
import com.atlas.auth.entity.User;
import com.atlas.auth.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public UserResponse getCurrentUser(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    public UserResponse getUser(Long id){
        User user=userRepository.findById(id)
                .orElseThrow(()->
                        new UsernameNotFoundException("User Not Found"));

        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail()
        );
    }
}
