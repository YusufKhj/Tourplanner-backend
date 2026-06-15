package com.example.Tourplanner.services;

import com.example.Tourplanner.dto.TourStatsResponseDTO;
import com.example.Tourplanner.entities.Tour;
import com.example.Tourplanner.entities.TourLog;
import com.example.Tourplanner.entities.Users;
import com.example.Tourplanner.exceptions.UserNotFoundException;
import com.example.Tourplanner.repository.TourLogRepository;
import com.example.Tourplanner.repository.TourRepository;
import com.example.Tourplanner.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourStatsServiceTest {

    @Mock
    private TourRepository tourRepository;
    @Mock
    private TourLogRepository tourLogRepository;
    @Mock
    private UsersRepository usersRepository;

    private TourStatsService tourStatsService;
    private Users testUser;

    @BeforeEach
    void setUp() {
        tourStatsService = new TourStatsService(tourRepository, tourLogRepository, usersRepository);
        testUser = new Users();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
    }

    @Test
    void getStats_shouldReturnEmptyStatsForNoTours() {
        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(tourRepository.findByUser(testUser)).thenReturn(List.of());

        TourStatsResponseDTO stats = tourStatsService.getStats("testuser");

        assertEquals(0, stats.totalTours());
        assertEquals(0, stats.totalDistanceKm());
        assertEquals(0, stats.averageDistanceKm());
        assertEquals(0, stats.averageRating());
        assertEquals(0, stats.totalLogs());
        assertEquals("N/A", stats.mostUsedTransportType());
    }

    @Test
    void getStats_shouldComputeCorrectValues() {
        Tour tour1 = new Tour();
        tour1.setId(UUID.randomUUID());
        tour1.setTourName("Tour A");
        tour1.setTransportType("car");
        tour1.setTourDistance(100.0);
        tour1.setEstimatedTime(120.0);
        tour1.setUser(testUser);

        Tour tour2 = new Tour();
        tour2.setId(UUID.randomUUID());
        tour2.setTourName("Tour B");
        tour2.setTransportType("bike");
        tour2.setTourDistance(50.0);
        tour2.setEstimatedTime(90.0);
        tour2.setUser(testUser);

        TourLog log1 = new TourLog();
        log1.setRating(4);
        log1.setTotalTime(60);
        log1.setTour(tour1);

        TourLog log2 = new TourLog();
        log2.setRating(5);
        log2.setTotalTime(120);
        log2.setTour(tour1);

        TourLog log3 = new TourLog();
        log3.setRating(3);
        log3.setTotalTime(45);
        log3.setTour(tour2);

        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(tourRepository.findByUser(testUser)).thenReturn(List.of(tour1, tour2));
        when(tourLogRepository.findByTour(tour1)).thenReturn(List.of(log1, log2));
        when(tourLogRepository.findByTour(tour2)).thenReturn(List.of(log3));

        TourStatsResponseDTO stats = tourStatsService.getStats("testuser");

        assertEquals(2, stats.totalTours());
        assertEquals(150.0, stats.totalDistanceKm());
        assertEquals(75.0, stats.averageDistanceKm());
        assertEquals(4.0, stats.averageRating());
        assertEquals(3, stats.totalLogs());
        assertEquals("car", stats.mostUsedTransportType());
        assertEquals(225, stats.totalDurationMinutes());
        assertEquals(2, stats.transportTypeDistribution().size());
    }

    @Test
    void getStats_shouldThrowWhenUserNotFound() {
        when(usersRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                tourStatsService.getStats("ghost"));
    }
}
