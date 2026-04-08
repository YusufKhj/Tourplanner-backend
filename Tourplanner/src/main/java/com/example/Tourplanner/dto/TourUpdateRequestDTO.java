package com.example.Tourplanner.dto;

import com.example.Tourplanner.entities.RouteInfo;
import lombok.Data;

@Data
public class TourUpdateRequestDTO {

    private String tourName;
    private String description;
    private String start;
    private String finish;
    private String transportType;
    private RouteInfo routeInfo;
}