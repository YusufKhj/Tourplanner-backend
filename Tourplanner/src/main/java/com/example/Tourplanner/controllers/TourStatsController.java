package com.example.Tourplanner.controllers;

import com.example.Tourplanner.dto.TourStatsResponseDTO;
import com.example.Tourplanner.services.TourStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class TourStatsController {

    private final TourStatsService tourStatsService;

    @GetMapping
    public ResponseEntity<TourStatsResponseDTO> getStats(@AuthenticationPrincipal String username) {
        return ResponseEntity.ok(tourStatsService.getStats(username));
    }
}
