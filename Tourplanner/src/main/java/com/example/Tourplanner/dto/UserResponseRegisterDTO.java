package com.example.Tourplanner.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserResponseRegisterDTO(String username, @NotBlank @Email String email) {}
