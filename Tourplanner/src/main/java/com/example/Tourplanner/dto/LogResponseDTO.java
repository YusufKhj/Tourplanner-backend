package com.example.Tourplanner.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record LogResponseDTO(
        UUID id,
        LocalDateTime dateTime,
        String comment,
        int difficulty,
        double totalDistance,
        int totalTime,
        int rating
) {}