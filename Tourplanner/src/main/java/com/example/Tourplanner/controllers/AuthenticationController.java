package com.example.Tourplanner.controllers;

import com.example.Tourplanner.dto.UserLoginRequestDTO;
import com.example.Tourplanner.dto.UserLoginResponseDTO;
import com.example.Tourplanner.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequestDTO request) {

        Optional<UserLoginResponseDTO> result = authenticationService.authenticate(request);

        if (result.isEmpty()) {
            return ResponseEntity.status(401).body("Login failed");
        }

        UserLoginResponseDTO user = result.get();

        ResponseCookie cookie = buildCookie(user.token());

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(user);
    }

    private ResponseCookie buildCookie(String token) {
        return ResponseCookie.from("auth_token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(3600)
                .build();
    }
}