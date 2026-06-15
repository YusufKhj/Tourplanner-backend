package com.example.Tourplanner.controllers;

import com.example.Tourplanner.dto.TourStatsResponseDTO;
import com.example.Tourplanner.services.TourStatsService;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TourStatsControllerTest {

    @Test
    void getStats_shouldReturnStatsFromService() {
        TourStatsService service = mock(TourStatsService.class);
        var expected = new TourStatsResponseDTO(
                2, 150.0, 75.0, 4.0, 3,
                "car", Map.of("car", 1L, "bike", 1L), 225
        );
        when(service.getStats("testuser")).thenReturn(expected);

        TourStatsController controller = new TourStatsController(service);
        var result = controller.getStats("testuser");

        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(2, result.getBody().totalTours());
        assertEquals(150.0, result.getBody().totalDistanceKm());
        assertEquals("car", result.getBody().mostUsedTransportType());
    }
}
