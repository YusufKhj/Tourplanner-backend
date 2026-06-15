package com.example.Tourplanner.services;

import com.example.Tourplanner.dto.TourCreateRequestDTO;
import com.example.Tourplanner.dto.TourResponseDTO;
import com.example.Tourplanner.dto.TourUpdateRequestDTO;
import com.example.Tourplanner.entities.RouteInfo;
import com.example.Tourplanner.entities.Tour;
import com.example.Tourplanner.entities.Users;
import com.example.Tourplanner.exceptions.TourNotFoundException;
import com.example.Tourplanner.exceptions.UnauthorizedException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourServiceTest {

    @Mock
    private TourRepository tourRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private RouteService routeService;

    private TourService tourService;
    private Users testUser;
    private Tour testTour;
    private UUID tourId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        tourService = new TourService(tourRepository, usersRepository, routeService);
        userId = UUID.randomUUID();
        tourId = UUID.randomUUID();

        testUser = new Users();
        testUser.setId(userId);
        testUser.setUsername("testuser");
        testUser.setPasswordHash("hash");
        testUser.setEmail("test@test.com");

        testTour = new Tour();
        testTour.setId(tourId);
        testTour.setTourName("Test Tour");
        testTour.setDescription("Description");
        testTour.setStart("Vienna");
        testTour.setFinish("Graz");
        testTour.setTransportType("car");
        testTour.setTourDistance(200.0);
        testTour.setEstimatedTime(120.0);
        testTour.setUser(testUser);
    }

    @Test
    void createTour_shouldCreateAndReturnTour() {
        TourCreateRequestDTO dto = new TourCreateRequestDTO();
        dto.setTourName("New Tour");
        dto.setDescription("Desc");
        dto.setStart("Vienna");
        dto.setFinish("Graz");
        dto.setTransportType("car");

        RouteInfo routeInfo = new RouteInfo(null, 100.0, 60.0);

        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(routeService.calculateRoute("Vienna", "Graz", "car")).thenReturn(routeInfo);

        Tour savedTour = new Tour();
        savedTour.setId(UUID.randomUUID());
        savedTour.setTourName("New Tour");
        savedTour.setDescription("Desc");
        savedTour.setStart("Vienna");
        savedTour.setFinish("Graz");
        savedTour.setTransportType("car");
        savedTour.setTourDistance(100.0);
        savedTour.setEstimatedTime(60.0);
        savedTour.setRouteInfo(routeInfo);
        savedTour.setUser(testUser);

        when(tourRepository.save(any(Tour.class))).thenReturn(savedTour);

        TourResponseDTO result = tourService.createTour(dto, "testuser");

        assertNotNull(result);
        assertEquals("New Tour", result.getTourName());
        verify(routeService, times(1)).calculateRoute("Vienna", "Graz", "car");
        verify(tourRepository, times(1)).save(any(Tour.class));
    }

    @Test
    void updateTour_shouldUpdateAndReturnTour() {
        TourUpdateRequestDTO dto = new TourUpdateRequestDTO();
        dto.setTourName("Updated");
        dto.setDescription("Updated Desc");
        dto.setStart("A");
        dto.setFinish("B");
        dto.setTransportType("bike");

        RouteInfo routeInfo = new RouteInfo(null, 50.0, 30.0);

        when(tourRepository.findById(tourId)).thenReturn(Optional.of(testTour));
        when(routeService.calculateRoute("A", "B", "bike")).thenReturn(routeInfo);

        Tour updatedTour = new Tour();
        updatedTour.setId(tourId);
        updatedTour.setTourName("Updated");
        updatedTour.setDescription("Updated Desc");
        updatedTour.setStart("A");
        updatedTour.setFinish("B");
        updatedTour.setTransportType("bike");
        updatedTour.setTourDistance(50.0);
        updatedTour.setEstimatedTime(30.0);
        updatedTour.setRouteInfo(routeInfo);
        updatedTour.setUser(testUser);

        when(tourRepository.save(any(Tour.class))).thenReturn(updatedTour);

        TourResponseDTO result = tourService.updateTour(tourId, dto, "testuser");

        assertEquals("Updated", result.getTourName());
        verify(routeService, times(1)).calculateRoute("A", "B", "bike");
        verify(tourRepository, times(1)).save(any(Tour.class));
    }

    @Test
    void updateTour_shouldThrowWhenNotOwner() {
        TourUpdateRequestDTO dto = new TourUpdateRequestDTO();
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(testTour));

        assertThrows(UnauthorizedException.class, () ->
                tourService.updateTour(tourId, dto, "otheruser"));
    }

    @Test
    void getTourById_shouldReturnTourWhenOwner() {
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(testTour));

        TourResponseDTO result = tourService.getTourById(tourId, "testuser");

        assertNotNull(result);
        assertEquals("Test Tour", result.getTourName());
    }

    @Test
    void getTourById_shouldThrowWhenNotOwner() {
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(testTour));

        assertThrows(UnauthorizedException.class, () ->
                tourService.getTourById(tourId, "otheruser"));
    }

    @Test
    void deleteTour_shouldDeleteWhenOwner() {
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(testTour));

        tourService.deleteTour(tourId, "testuser");

        verify(tourRepository, times(1)).deleteById(tourId);
    }

    @Test
    void deleteTour_shouldThrowWhenNotOwner() {
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(testTour));

        assertThrows(UnauthorizedException.class, () ->
                tourService.deleteTour(tourId, "otheruser"));
    }

    @Test
    void getToursByUser_shouldReturnList() {
        when(tourRepository.findByUser(testUser)).thenReturn(List.of(testTour));

        List<TourResponseDTO> result = tourService.getToursByUser(testUser);

        assertEquals(1, result.size());
        assertEquals("Test Tour", result.getFirst().getTourName());
    }
}
