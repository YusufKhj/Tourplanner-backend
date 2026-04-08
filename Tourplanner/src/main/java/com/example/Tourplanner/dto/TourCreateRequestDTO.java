package com.example.Tourplanner.dto;

import com.example.Tourplanner.entities.RouteInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourCreateRequestDTO {

    private String tourName;
    private String description;
    private String start;
    private String finish;
    private String transportType;
    private RouteInfo routeInfo; // distance und duration kommen von API
}