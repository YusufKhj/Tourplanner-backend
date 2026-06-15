package com.example.Tourplanner.controllers;

import com.example.Tourplanner.dto.TourCreateRequestDTO;
import com.example.Tourplanner.dto.TourResponseDTO;
import com.example.Tourplanner.dto.TourUpdateRequestDTO;
import com.example.Tourplanner.entities.Users;
import com.example.Tourplanner.services.ImageService;
import com.example.Tourplanner.services.TourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tour")
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;
    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<TourResponseDTO> createTour(
            @Valid @RequestBody TourCreateRequestDTO requestDTO,
            @AuthenticationPrincipal String username
    ) {
        TourResponseDTO responseDTO = tourService.createTour(requestDTO, username);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TourResponseDTO> updateTour(
            @PathVariable UUID id,
            @Valid @RequestBody TourUpdateRequestDTO dto,
            @AuthenticationPrincipal String username) {

        return ResponseEntity.ok(
                tourService.updateTour(id, dto, username)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourResponseDTO> getTourById(
            @PathVariable UUID id,
            @AuthenticationPrincipal String username) {
        TourResponseDTO responseDTO = tourService.getTourById(id, username);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TourResponseDTO>> getToursByUser(@PathVariable UUID userId) {
        Users user = new Users();
        user.setId(userId);

        List<TourResponseDTO> tours = tourService.getToursByUser(user);
        return ResponseEntity.ok(tours);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(
            @PathVariable UUID id,
            @AuthenticationPrincipal String username) {
        tourService.deleteTour(id, username);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal String username) {
        String filename = imageService.saveImage(id, file, username);
        return ResponseEntity.ok(filename);
    }

    @GetMapping("/image/{filename}")
    public ResponseEntity<Resource> getImageByFilename(@PathVariable String filename) {
        Resource resource = imageService.loadImage(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<Void> deleteImage(
            @PathVariable UUID id,
            @AuthenticationPrincipal String username) {
        imageService.deleteImage(id, username);
        return ResponseEntity.noContent().build();
    }
}