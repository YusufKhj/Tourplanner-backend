package com.example.Tourplanner.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TourUpdateRequestDTO {

    @NotBlank(message = "Tour name is required")
    private String tourName;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Start location is required")
    private String start;

    @NotBlank(message = "Finish location is required")
    private String finish;

    @NotBlank(message = "Transport type is required")
    private String transportType;
}