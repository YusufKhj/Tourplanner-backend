package com.example.Tourplanner.services;

import com.example.Tourplanner.dto.LogCreateRequestDTO;
import com.example.Tourplanner.dto.LogResponseDTO;
import com.example.Tourplanner.dto.LogUpdateRequestDTO;
import com.example.Tourplanner.entities.Tour;
import com.example.Tourplanner.entities.TourLog;
import com.example.Tourplanner.entities.Users;
import com.example.Tourplanner.exceptions.UnauthorizedException;
import com.example.Tourplanner.repository.TourLogRepository;
import com.example.Tourplanner.repository.TourRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourLogServiceTest {

    @Mock
    private TourLogRepository tourLogRepository;
    @Mock
    private TourRepository tourRepository;
    @Mock
    private ComputedAttributeService computedAttributeService;

    private TourLogService tourLogService;
    private Users testUser;
    private Tour testTour;
    private TourLog testLog;
    private UUID tourId;
    private UUID logId;

    @BeforeEach
    void setUp() {
        tourLogService = new TourLogService(tourLogRepository, tourRepository, computedAttributeService);
        tourId = UUID.randomUUID();
        logId = UUID.randomUUID();

        testUser = new Users();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");

        testTour = new Tour();
        testTour.setId(tourId);
        testTour.setTourName("Test Tour");
        testTour.setUser(testUser);

        testLog = new TourLog();
        testLog.setId(logId);
        testLog.setDateTime(LocalDateTime.now());
        testLog.setComment("Nice tour");
        testLog.setDifficulty(2);
        testLog.setTotalDistance(15.0);
        testLog.setTotalTime(90);
        testLog.setRating(4);
        testLog.setTour(testTour);
    }

    @Test
    void createLog_shouldCreateAndReturnLog() {
        LogCreateRequestDTO dto = new LogCreateRequestDTO();
        dto.setDateTime(LocalDateTime.now());
        dto.setComment("Great");
        dto.setDifficulty(1);
        dto.setTotalDistance(10.0);
        dto.setTotalTime(60);
        dto.setRating(5);

        when(tourRepository.findById(tourId)).thenReturn(Optional.of(testTour));

        TourLog savedLog = new TourLog();
        savedLog.setId(UUID.randomUUID());
        savedLog.setDateTime(dto.getDateTime());
        savedLog.setComment("Great");
        savedLog.setDifficulty(1);
        savedLog.setTotalDistance(10.0);
        savedLog.setTotalTime(60);
        savedLog.setRating(5);
        savedLog.setTour(testTour);

        when(tourLogRepository.save(any(TourLog.class))).thenReturn(savedLog);

        LogResponseDTO result = tourLogService.createLog(tourId, dto, "testuser");

        assertNotNull(result);
        assertEquals("Great", result.comment());
        verify(computedAttributeService, times(1)).recompute(tourId);
    }

    @Test
    void createLog_shouldThrowWhenNotOwner() {
        LogCreateRequestDTO dto = new LogCreateRequestDTO();
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(testTour));

        assertThrows(UnauthorizedException.class, () ->
                tourLogService.createLog(tourId, dto, "otheruser"));
    }

    @Test
    void updateLog_shouldUpdateAndReturnLog() {
        LogUpdateRequestDTO dto = new LogUpdateRequestDTO();
        dto.setComment("Updated comment");
        dto.setDifficulty(3);
        dto.setTotalDistance(20.0);
        dto.setTotalTime(120);
        dto.setRating(3);

        when(tourLogRepository.findById(logId)).thenReturn(Optional.of(testLog));
        when(tourLogRepository.save(any(TourLog.class))).thenReturn(testLog);

        LogResponseDTO result = tourLogService.updateLog(logId, dto, "testuser");

        assertEquals("Updated comment", result.comment());
        verify(computedAttributeService, times(1)).recompute(tourId);
    }

    @Test
    void updateLog_shouldThrowWhenNotOwner() {
        LogUpdateRequestDTO dto = new LogUpdateRequestDTO();
        when(tourLogRepository.findById(logId)).thenReturn(Optional.of(testLog));

        assertThrows(UnauthorizedException.class, () ->
                tourLogService.updateLog(logId, dto, "otheruser"));
    }

    @Test
    void deleteLog_shouldDeleteAndRecompute() {
        when(tourLogRepository.findById(logId)).thenReturn(Optional.of(testLog));

        tourLogService.deleteLog(logId, "testuser");

        verify(tourLogRepository, times(1)).delete(testLog);
        verify(computedAttributeService, times(1)).recompute(tourId);
    }

    @Test
    void deleteLog_shouldThrowWhenNotOwner() {
        when(tourLogRepository.findById(logId)).thenReturn(Optional.of(testLog));

        assertThrows(UnauthorizedException.class, () ->
                tourLogService.deleteLog(logId, "otheruser"));
    }

    @Test
    void getLogsByTour_shouldReturnList() {
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(testTour));
        when(tourLogRepository.findByTour(testTour)).thenReturn(List.of(testLog));

        List<LogResponseDTO> result = tourLogService.getLogsByTour(tourId, "testuser");

        assertEquals(1, result.size());
        assertEquals("Nice tour", result.getFirst().comment());
    }

    @Test
    void getLogById_shouldReturnLog() {
        when(tourLogRepository.findById(logId)).thenReturn(Optional.of(testLog));

        LogResponseDTO result = tourLogService.getLogById(logId, "testuser");

        assertEquals("Nice tour", result.comment());
    }
}
