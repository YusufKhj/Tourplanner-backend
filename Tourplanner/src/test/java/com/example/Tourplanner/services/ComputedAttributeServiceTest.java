package com.example.Tourplanner.services;

import com.example.Tourplanner.entities.Tour;
import com.example.Tourplanner.entities.TourLog;
import com.example.Tourplanner.repository.TourLogRepository;
import com.example.Tourplanner.repository.TourRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComputedAttributeServiceTest {

    @Mock
    private TourLogRepository tourLogRepository;
    @Mock
    private TourRepository tourRepository;

    private ComputedAttributeService service;
    private Tour testTour;
    private UUID tourId;

    @BeforeEach
    void setUp() {
        service = new ComputedAttributeService(tourLogRepository, tourRepository);
        tourId = UUID.randomUUID();
        testTour = new Tour();
        testTour.setId(tourId);
    }

    @Test
    void recompute_shouldSetPopularity0WhenNoLogs() {
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(testTour));
        when(tourLogRepository.findByTour(testTour)).thenReturn(List.of());

        service.recompute(tourId);

        assertEquals(0, testTour.getPopularity());
        assertFalse(testTour.isChildFriendly());
        verify(tourRepository, times(1)).save(testTour);
    }

    @Test
    void recompute_shouldSetPopularity3WhenManyLogs() {
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(testTour));
        List<TourLog> logs = List.of(
                createLog(1, 5.0, 30),
                createLog(2, 8.0, 45),
                createLog(1, 3.0, 20),
                createLog(3, 12.0, 50),
                createLog(2, 6.0, 35),
                createLog(1, 4.0, 25)
        );
        when(tourLogRepository.findByTour(testTour)).thenReturn(logs);

        service.recompute(tourId);

        assertEquals(3, testTour.getPopularity());
        assertTrue(testTour.isChildFriendly());
    }

    @Test
    void recompute_shouldSetNotChildFriendlyWhenHighDifficulty() {
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(testTour));
        List<TourLog> logs = List.of(
                createLog(5, 5.0, 30),
                createLog(4, 8.0, 45)
        );
        when(tourLogRepository.findByTour(testTour)).thenReturn(logs);

        service.recompute(tourId);

        assertFalse(testTour.isChildFriendly());
    }

    @Test
    void recompute_shouldSetNotChildFriendlyWhenLongDistance() {
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(testTour));
        List<TourLog> logs = List.of(
                createLog(1, 50.0, 30),
                createLog(2, 80.0, 45)
        );
        when(tourLogRepository.findByTour(testTour)).thenReturn(logs);

        service.recompute(tourId);

        assertFalse(testTour.isChildFriendly());
    }

    private TourLog createLog(int difficulty, double distance, int time) {
        TourLog log = new TourLog();
        log.setDifficulty(difficulty);
        log.setTotalDistance(distance);
        log.setTotalTime(time);
        return log;
    }
}
