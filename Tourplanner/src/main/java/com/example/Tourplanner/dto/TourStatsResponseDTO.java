package com.example.Tourplanner.dto;

import java.util.Map;

public record TourStatsResponseDTO(
        long totalTours,
        double totalDistanceKm,
        double averageDistanceKm,
        double averageRating,
        long totalLogs,
        String mostUsedTransportType,
        Map<String, Long> transportTypeDistribution,
        int totalDurationMinutes
) {}
