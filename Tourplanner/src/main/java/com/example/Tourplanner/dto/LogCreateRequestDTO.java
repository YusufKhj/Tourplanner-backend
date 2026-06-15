package com.example.Tourplanner.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LogCreateRequestDTO {

    @NotNull(message = "Date/time is required")
    private LocalDateTime dateTime;

    @NotBlank(message = "Comment is required")
    private String comment;

    @Min(value = 1, message = "Difficulty must be at least 1")
    @Max(value = 5, message = "Difficulty must be at most 5")
    private int difficulty;

    @Min(value = 0, message = "Total distance must be non-negative")
    private double totalDistance;

    @Min(value = 0, message = "Total time must be non-negative")
    private int totalTime;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private int rating;

    private String tourId;
}