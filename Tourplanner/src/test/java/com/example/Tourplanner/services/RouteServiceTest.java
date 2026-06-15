package com.example.Tourplanner.services;

import com.example.Tourplanner.entities.RouteInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Test
    void calculateRoute_shouldReturnApproximateRouteWhenNoApiKey() {
        RouteService routeService = new RouteService();
        RouteInfo result = routeService.calculateRoute("Vienna", "Graz", "car");
        assertNotNull(result);
        assertTrue(result.getDistance() > 0);
        assertTrue(result.getDuration() > 0);
    }
}
