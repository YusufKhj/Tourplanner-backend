package com.example.Tourplanner.controllers;

import com.example.Tourplanner.dto.LogCreateRequestDTO;
import com.example.Tourplanner.dto.LogResponseDTO;
import com.example.Tourplanner.dto.LogUpdateRequestDTO;
import com.example.Tourplanner.services.TourLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TourLogController {

    private final TourLogService tourLogService;

    @PostMapping("/api/tour/{tourId}/log")
    public ResponseEntity<LogResponseDTO> createLog(
            @PathVariable UUID tourId,
            @Valid @RequestBody LogCreateRequestDTO dto,
            @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(tourLogService.createLog(tourId, dto, username));
    }

    @PutMapping("/api/log/{id}")
    public ResponseEntity<LogResponseDTO> updateLog(
            @PathVariable UUID id,
            @Valid @RequestBody LogUpdateRequestDTO dto,
            @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(tourLogService.updateLog(id, dto, username));
    }

    @DeleteMapping("/api/log/{id}")
    public ResponseEntity<Void> deleteLog(
            @PathVariable UUID id,
            @AuthenticationPrincipal String username) {
        tourLogService.deleteLog(id, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/tour/{tourId}/logs")
    public ResponseEntity<List<LogResponseDTO>> getLogsByTour(
            @PathVariable UUID tourId,
            @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(tourLogService.getLogsByTour(tourId, username));
    }

    @GetMapping("/api/log/{id}")
    public ResponseEntity<LogResponseDTO> getLogById(
            @PathVariable UUID id,
            @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(tourLogService.getLogById(id, username));
    }
}
