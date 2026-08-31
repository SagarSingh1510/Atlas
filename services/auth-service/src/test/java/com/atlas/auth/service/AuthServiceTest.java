package com.atlas.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.atlas.auth.dto.RegisterRequest;
import com.atlas.auth.dto.RegisterResponse;
import com.atlas.auth.entity.User;
import com.atlas.auth.exception.DuplicateUserException;
import com.atlas.auth.repository.UserRepository;
import com.atlas.auth.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void registerHashesPasswordPersistsUserAndReturnsSafeResponse() {
        RegisterRequest request = new RegisterRequest("sagar", "sagar@example.com", "password123");
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterResponse response = authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getUsername()).isEqualTo("sagar");
        assertThat(savedUser.getEmail()).isEqualTo("sagar@example.com");
        assertThat(savedUser.getPasswordHash()).isEqualTo("hashed-password");
        assertThat(response.username()).isEqualTo("sagar");
        assertThat(response.email()).isEqualTo("sagar@example.com");
    }

    @Test
    void registerRejectsDuplicateUsernameBeforeHashingPassword() {
        RegisterRequest request = new RegisterRequest("sagar", "sagar@example.com", "password123");
        when(userRepository.existsByUsername("sagar")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateUserException.class)
                .hasMessage("Username already exists.");

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerRejectsDuplicateEmailBeforeHashingPassword() {
        RegisterRequest request = new RegisterRequest("sagar", "sagar@example.com", "password123");
        when(userRepository.existsByEmail("sagar@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateUserException.class)
                .hasMessage("Email already exists.");

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerTranslatesDatabaseUniquenessRaceToDuplicateUserException() {
        RegisterRequest request = new RegisterRequest("sagar", "sagar@example.com", "password123");
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("unique constraint"));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateUserException.class)
                .hasMessage("Username or email already exists.");
    }
}
