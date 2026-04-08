package com.example.Tourplanner.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UserLoginResponseDTO (@NotBlank UUID id, @NotBlank String username, @NotBlank @Email String email,@NotBlank String token){ }
