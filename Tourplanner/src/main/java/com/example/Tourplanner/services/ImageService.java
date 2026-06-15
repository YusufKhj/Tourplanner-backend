package com.example.Tourplanner.services;

import com.example.Tourplanner.entities.Tour;
import com.example.Tourplanner.exceptions.ImageNotFoundException;
import com.example.Tourplanner.exceptions.TourNotFoundException;
import com.example.Tourplanner.exceptions.UnauthorizedException;
import com.example.Tourplanner.repository.TourRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final Logger log = LoggerFactory.getLogger(ImageService.class);

    @Value("${app.image.directory}")
    private String imageDirectory;

    private final TourRepository tourRepository;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Path.of(imageDirectory));
            log.info("Image directory initialized: {}", imageDirectory);
        } catch (IOException e) {
            log.error("Could not create image directory: {}", imageDirectory, e);
            throw new RuntimeException("Could not create image directory: " + imageDirectory, e);
        }
    }

    public String saveImage(UUID tourId, MultipartFile file, String username) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new TourNotFoundException("Tour not found: " + tourId));

        if (!tour.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("User " + username + " not authorized to upload image for tour " + tourId);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = UUID.randomUUID() + extension;
        Path targetPath = Path.of(imageDirectory, filename);

        try {
            Files.copy(file.getInputStream(), targetPath);

            if (tour.getImagePath() != null) {
                Path oldPath = Path.of(imageDirectory, tour.getImagePath());
                Files.deleteIfExists(oldPath);
                log.debug("Deleted old image: {}", tour.getImagePath());
            }

            tour.setImagePath(filename);
            tourRepository.save(tour);

            log.info("Image saved: tourId={}, filename={}, user={}", tourId, filename, username);
            return filename;
        } catch (IOException e) {
            log.error("Failed to save image for tourId={}", tourId, e);
            throw new RuntimeException("Failed to save image", e);
        }
    }

    public Resource loadImage(String filename) {
        Path path = Path.of(imageDirectory, filename);
        if (!Files.exists(path)) {
            throw new ImageNotFoundException("Image not found: " + filename);
        }
        return new FileSystemResource(path);
    }

    public void deleteImage(UUID tourId, String username) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new TourNotFoundException("Tour not found: " + tourId));

        if (!tour.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("User " + username + " not authorized to delete image for tour " + tourId);
        }

        if (tour.getImagePath() != null) {
            try {
                Path path = Path.of(imageDirectory, tour.getImagePath());
                Files.deleteIfExists(path);
                tour.setImagePath(null);
                tourRepository.save(tour);
                log.info("Image deleted: tourId={}, user={}", tourId, username);
            } catch (IOException e) {
                log.error("Failed to delete image for tourId={}", tourId, e);
                throw new RuntimeException("Failed to delete image", e);
            }
        }
    }
}
