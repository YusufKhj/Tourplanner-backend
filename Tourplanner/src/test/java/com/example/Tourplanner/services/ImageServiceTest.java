package com.example.Tourplanner.services;

import com.example.Tourplanner.entities.Tour;
import com.example.Tourplanner.entities.Users;
import com.example.Tourplanner.exceptions.ImageNotFoundException;
import com.example.Tourplanner.exceptions.TourNotFoundException;
import com.example.Tourplanner.exceptions.UnauthorizedException;
import com.example.Tourplanner.repository.TourRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private TourRepository tourRepository;

    private ImageService imageService;
    private Users testUser;
    private Tour testTour;
    private UUID tourId;
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        imageService = new ImageService(tourRepository);
        tourId = UUID.randomUUID();

        testUser = new Users();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");

        testTour = new Tour();
        testTour.setId(tourId);
        testTour.setUser(testUser);

        tempDir = Files.createTempDirectory("image-test-");
        ReflectionTestUtils.setField(imageService, "imageDirectory", tempDir.toString());
    }

    @Test
    void saveImage_shouldThrowWhenTourNotFound() {
        var file = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[]{1, 2, 3});
        when(tourRepository.findById(tourId)).thenReturn(Optional.empty());

        assertThrows(TourNotFoundException.class, () ->
                imageService.saveImage(tourId, file, "testuser"));
    }

    @Test
    void saveImage_shouldThrowWhenUnauthorized() {
        var file = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[]{1, 2, 3});
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(testTour));

        assertThrows(UnauthorizedException.class, () ->
                imageService.saveImage(tourId, file, "otheruser"));
    }

    @Test
    void loadImage_shouldThrowWhenNotFound() {
        assertThrows(ImageNotFoundException.class, () ->
                imageService.loadImage("nonexistent.jpg"));
    }
}
