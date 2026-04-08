package com.example.Tourplanner.controller;

import com.example.Tourplanner.dto.TourCreateRequestDTO;
import com.example.Tourplanner.dto.TourResponseDTO;
import com.example.Tourplanner.dto.TourUpdateRequestDTO;
import com.example.Tourplanner.entities.Users;
import com.example.Tourplanner.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tour")
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;

    @PostMapping
    public ResponseEntity<TourResponseDTO> createTour(
            @RequestBody TourCreateRequestDTO requestDTO,
            @AuthenticationPrincipal String username
    ) {
        TourResponseDTO responseDTO = tourService.createTour(requestDTO, username);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TourResponseDTO> updateTour(
            @PathVariable UUID id,
            @RequestBody TourUpdateRequestDTO dto,
            Principal principal) {

        return ResponseEntity.ok(
                tourService.updateTour(id, dto, principal.getName())
        );
    }

    // einzelne Tour
    @GetMapping("/{id}")
    public ResponseEntity<TourResponseDTO> getTourById(@PathVariable UUID id) {
        TourResponseDTO responseDTO = tourService.getTourById(id);
        return ResponseEntity.ok(responseDTO);
    }

    // alle Touren eines Users
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TourResponseDTO>> getToursByUser(@PathVariable UUID userId) {
        Users user = new Users();
        user.setId(userId);

        List<TourResponseDTO> tours = tourService.getToursByUser(user);
        return ResponseEntity.ok(tours);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable UUID id) {
        tourService.deleteTour(id);
        return ResponseEntity.noContent().build();
    }
}