package com.example.Tourplanner.services;

import com.example.Tourplanner.dto.UserLoginRequestDTO;
import com.example.Tourplanner.dto.UserLoginResponseDTO;
import com.example.Tourplanner.entities.Users;
import com.example.Tourplanner.repository.UsersRepository;
import com.example.Tourplanner.utils.JWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private BCryptPasswordEncoder encoder;
    @Mock
    private JWT jwt;

    private AuthenticationService authService;
    private Users testUser;

    @BeforeEach
    void setUp() {
        authService = new AuthenticationService(usersRepository, encoder, jwt);
        testUser = new Users();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setEmail("test@test.com");
        testUser.setPasswordHash("encodedHash");
    }

    @Test
    void authenticate_shouldReturnToken() {
        UserLoginRequestDTO request = new UserLoginRequestDTO("testuser", "password");

        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(encoder.matches("password", "encodedHash")).thenReturn(true);
        when(jwt.createToken("testuser")).thenReturn("jwt-token");

        Optional<UserLoginResponseDTO> result = authService.authenticate(request);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().username());
        assertEquals("jwt-token", result.get().token());
    }

    @Test
    void authenticate_shouldReturnEmptyWhenUserNotFound() {
        UserLoginRequestDTO request = new UserLoginRequestDTO("unknown", "password");

        when(usersRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<UserLoginResponseDTO> result = authService.authenticate(request);

        assertTrue(result.isEmpty());
    }

    @Test
    void authenticate_shouldReturnEmptyWhenWrongPassword() {
        UserLoginRequestDTO request = new UserLoginRequestDTO("testuser", "wrong");

        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(encoder.matches("wrong", "encodedHash")).thenReturn(false);

        Optional<UserLoginResponseDTO> result = authService.authenticate(request);

        assertTrue(result.isEmpty());
    }
}
