package com.example.Tourplanner.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDTO (@NotBlank String username, @NotBlank String password) {}
