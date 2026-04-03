package com.example.Tourplanner.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegisterResponseDTO(String username, @NotBlank @Email String email) {}
