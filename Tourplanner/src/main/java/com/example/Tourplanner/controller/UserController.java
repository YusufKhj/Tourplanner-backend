package com.example.Tourplanner.controller;

import com.example.Tourplanner.dto.UserRequestRegisterDTO;
import com.example.Tourplanner.dto.UserResponseRegisterDTO;
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
    public ResponseEntity<UserResponseRegisterDTO> register(
            @Valid @RequestBody UserRequestRegisterDTO dto) {

        userService.registerUser(dto);

        // Response zurückgeben
        UserResponseRegisterDTO response =
                new UserResponseRegisterDTO(dto.username(), dto.email());

        return ResponseEntity.ok(response);
    }
}