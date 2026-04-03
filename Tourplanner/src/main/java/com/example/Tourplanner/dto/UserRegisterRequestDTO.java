package com.example.Tourplanner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public record UserRegisterRequestDTO (
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank @Email String email,
        @NotBlank String passwordConfirmation
)
{}
