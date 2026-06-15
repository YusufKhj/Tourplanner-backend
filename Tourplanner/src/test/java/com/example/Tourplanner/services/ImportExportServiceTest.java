package com.example.Tourplanner.services;

import com.example.Tourplanner.entities.Tour;
import com.example.Tourplanner.entities.Users;
import com.example.Tourplanner.repository.TourRepository;
import com.example.Tourplanner.repository.UsersRepository;
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
class ImportExportServiceTest {

    @Mock
    private TourRepository tourRepository;
    @Mock
    private TourLogService tourLogService;
    @Mock
    private TourService tourService;
    @Mock
    private UsersRepository usersRepository;

    private ImportExportService importExportService;
    private Users testUser;

    @BeforeEach
    void setUp() {
        importExportService = new ImportExportService(tourRepository, tourLogService, tourService, usersRepository);
        testUser = new Users();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
    }

    @Test
    void exportTours_shouldReturnJson() {
        Tour tour = new Tour();
        tour.setId(UUID.randomUUID());
        tour.setTourName("Export Tour");
        tour.setDescription("Desc");
        tour.setStart("A");
        tour.setFinish("B");
        tour.setTransportType("car");
        tour.setUser(testUser);

        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(tourRepository.findByUser(testUser)).thenReturn(List.of(tour));
        when(tourLogService.getLogsByTour(tour.getId(), "testuser")).thenReturn(List.of());

        String json = importExportService.exportTours("testuser");

        assertNotNull(json);
        assertTrue(json.contains("Export Tour"));
        assertTrue(json.contains("car"));
        verify(usersRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void exportTours_shouldReturnEmptyArrayForNoTours() {
        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(tourRepository.findByUser(testUser)).thenReturn(List.of());

        String json = importExportService.exportTours("testuser");

        assertNotNull(json);
        assertTrue(json.trim().startsWith("[") && json.trim().endsWith("]"));
        assertFalse(json.contains("Export Tour"));
    }

    @Test
    void importTours_shouldImportValidJson() {
        String json = """
                [{
                    "tourName": "Imported Tour",
                    "description": "Imported",
                    "start": "X",
                    "finish": "Y",
                    "transportType": "bike",
                    "logs": []
                }]
                """;

        var responseDto = new com.example.Tourplanner.dto.TourResponseDTO(
                UUID.randomUUID(), "Imported Tour", "Imported", "X", "Y", "bike",
                100.0, 60.0, null, testUser.getId());

        when(tourService.createTour(any(), eq("testuser"))).thenReturn(responseDto);

        int count = importExportService.importTours(json, "testuser");

        assertEquals(1, count);
        verify(tourService, times(1)).createTour(any(), eq("testuser"));
    }
}
