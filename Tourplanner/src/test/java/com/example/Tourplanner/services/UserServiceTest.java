package com.example.Tourplanner.services;

import com.example.Tourplanner.dto.UserRegisterRequestDTO;
import com.example.Tourplanner.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_shouldSucceed() {
        UserRegisterRequestDTO dto = new UserRegisterRequestDTO("newuser", "pass123", "new@test.com", "pass123");

        when(usersRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(usersRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("encodedHash");

        userService.registerUser(dto);

        verify(usersRepository, times(1)).save(any());
    }

    @Test
    void registerUser_shouldThrowWhenUsernameExists() {
        UserRegisterRequestDTO dto = new UserRegisterRequestDTO("existing", "pass123", "new@test.com", "pass123");
        when(usersRepository.findByUsername("existing")).thenReturn(Optional.of(new com.example.Tourplanner.entities.Users()));

        assertThrows(ResponseStatusException.class, () -> userService.registerUser(dto));
    }

    @Test
    void registerUser_shouldThrowWhenEmailExists() {
        UserRegisterRequestDTO dto = new UserRegisterRequestDTO("newuser", "pass123", "dup@test.com", "pass123");
        when(usersRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(usersRepository.findByEmail("dup@test.com")).thenReturn(Optional.of(new com.example.Tourplanner.entities.Users()));

        assertThrows(ResponseStatusException.class, () -> userService.registerUser(dto));
    }

    @Test
    void registerUser_shouldThrowWhenPasswordMismatch() {
        UserRegisterRequestDTO dto = new UserRegisterRequestDTO("newuser", "pass123", "new@test.com", "different");
        when(usersRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(usersRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.registerUser(dto));
    }
}
