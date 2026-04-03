package com.example.Tourplanner.controller;

import com.example.Tourplanner.dto.UserLoginRequestDTO;
import com.example.Tourplanner.utils.APIResponseUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import com.example.Tourplanner.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authservice;

    @PostMapping("/login")
    public ResponseEntity<?> login (@Valid @RequestBody UserLoginRequestDTO dto){

        String token = authservice.loginUser(dto);

        if (token == null) {
            return APIResponseUtil.error("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }

        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false) // true in production (HTTPS)
                .path("/")
                .maxAge(60 * 60) // 1 hour
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(APIResponseUtil.success(token, "Login successful"));
    }
}
