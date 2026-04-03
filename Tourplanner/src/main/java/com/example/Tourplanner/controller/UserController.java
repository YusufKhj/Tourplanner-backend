package com.example.Tourplanner.controller;

import com.example.Tourplanner.dto.UserRegisterRequestDTO;
import com.example.Tourplanner.dto.UserRegisterResponseDTO;
import com.example.Tourplanner.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponseDTO> register(
            @Valid @RequestBody UserRegisterRequestDTO dto) {

        userService.registerUser(dto);

        UserRegisterResponseDTO response =
                new UserRegisterResponseDTO(dto.username(), dto.email());

        return ResponseEntity.ok(response);
    }
}