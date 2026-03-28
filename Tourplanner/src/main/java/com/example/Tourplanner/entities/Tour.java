package com.example.Tourplanner.entities;


import java.time.LocalDate;
import java.util.UUID;
import java.util.Map;

public class Tour {
    private UUID id;
    private String tourName;
    private String description;
    private String from;
    private String to;
    private String transportType;
    private double tourDistance;
    private LocalDate estimatedTime;
    private Map<String, Object> RouteInfo;
}
