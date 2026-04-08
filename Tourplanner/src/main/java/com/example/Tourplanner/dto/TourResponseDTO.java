package com.example.Tourplanner.dto;

import com.example.Tourplanner.entities.RouteInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourResponseDTO {

    private UUID id;
    private String tourName;
    private String description;
    private String start;
    private String finish;
    private String transportType;
    private double tourDistance;
    private double estimatedTime;
    private RouteInfo routeInfo;
    private UUID userId;
}