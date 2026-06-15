package com.example.Tourplanner.services;

import com.example.Tourplanner.dto.TourCreateRequestDTO;
import com.example.Tourplanner.dto.TourResponseDTO;
import com.example.Tourplanner.dto.TourUpdateRequestDTO;
import com.example.Tourplanner.entities.RouteInfo;
import com.example.Tourplanner.entities.Tour;
import com.example.Tourplanner.entities.Users;
import com.example.Tourplanner.exceptions.TourNotFoundException;
import com.example.Tourplanner.exceptions.UnauthorizedException;
import com.example.Tourplanner.exceptions.UserNotFoundException;
import com.example.Tourplanner.repository.TourRepository;
import com.example.Tourplanner.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourService {

    private static final Logger log = LoggerFactory.getLogger(TourService.class);

    private final TourRepository tourRepository;
    private final UsersRepository usersRepository;
    private final RouteService routeService;

    public TourResponseDTO createTour(TourCreateRequestDTO requestDTO, String username) {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        RouteInfo routeInfo = routeService.calculateRoute(
                requestDTO.getStart(),
                requestDTO.getFinish(),
                requestDTO.getTransportType()
        );

        Tour tour = new Tour();
        tour.setTourName(requestDTO.getTourName());
        tour.setDescription(requestDTO.getDescription());
        tour.setStart(requestDTO.getStart());
        tour.setFinish(requestDTO.getFinish());
        tour.setTransportType(requestDTO.getTransportType());
        tour.setRouteInfo(routeInfo);
        tour.setTourDistance(routeInfo.getDistance());
        tour.setEstimatedTime(routeInfo.getDuration());
        tour.setUser(user);

        Tour savedTour = tourRepository.save(tour);
        log.info("Tour created: id={}, name='{}', user={}", savedTour.getId(), savedTour.getTourName(), username);
        return mapToResponseDTO(savedTour);
    }

    public TourResponseDTO updateTour(UUID id, TourUpdateRequestDTO dto, String username) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new TourNotFoundException("Tour not found: " + id));

        if (!tour.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("User " + username + " not authorized to update tour " + id);
        }

        RouteInfo routeInfo = routeService.calculateRoute(
                dto.getStart(),
                dto.getFinish(),
                dto.getTransportType()
        );

        tour.setTourName(dto.getTourName());
        tour.setDescription(dto.getDescription());
        tour.setStart(dto.getStart());
        tour.setFinish(dto.getFinish());
        tour.setTransportType(dto.getTransportType());
        tour.setRouteInfo(routeInfo);
        tour.setTourDistance(routeInfo.getDistance());
        tour.setEstimatedTime(routeInfo.getDuration());

        Tour updatedTour = tourRepository.save(tour);
        log.info("Tour updated: id={}, name='{}', user={}", id, dto.getTourName(), username);
        return mapToResponseDTO(updatedTour);
    }

    public List<TourResponseDTO> getToursByUser(Users user) {
        List<Tour> tours = tourRepository.findByUser(user);
        log.debug("Fetched {} tours for user id={}", tours.size(), user.getId());
        return tours.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    public TourResponseDTO getTourById(UUID id, String username) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new TourNotFoundException("Tour not found: " + id));

        if (!tour.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("User " + username + " not authorized to view tour " + id);
        }

        return mapToResponseDTO(tour);
    }

    public void deleteTour(UUID id, String username) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new TourNotFoundException("Tour not found: " + id));

        if (!tour.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("User " + username + " not authorized to delete tour " + id);
        }

        tourRepository.deleteById(id);
        log.info("Tour deleted: id={}, user={}", id, username);
    }

    private TourResponseDTO mapToResponseDTO(Tour tour) {
        return new TourResponseDTO(
                tour.getId(),
                tour.getTourName(),
                tour.getDescription(),
                tour.getStart(),
                tour.getFinish(),
                tour.getTransportType(),
                tour.getTourDistance(),
                tour.getEstimatedTime(),
                tour.getRouteInfo(),
                tour.getUser().getId()
        );
    }
}
