package com.example.Tourplanner.service;

import com.example.Tourplanner.dto.TourCreateRequestDTO;
import com.example.Tourplanner.dto.TourResponseDTO;
import com.example.Tourplanner.entities.Tour;
import com.example.Tourplanner.entities.Users;
import com.example.Tourplanner.repository.TourRepository;
import com.example.Tourplanner.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.Tourplanner.dto.TourUpdateRequestDTO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourService {

    private final TourRepository tourRepository;
    private final UsersRepository usersRepository;

    public TourResponseDTO createTour(TourCreateRequestDTO requestDTO, String username) {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Tour tour = new Tour();

        tour.setTourName(requestDTO.getTourName());
        tour.setDescription(requestDTO.getDescription());
        tour.setStart(requestDTO.getStart());
        tour.setFinish(requestDTO.getFinish());
        tour.setTransportType(requestDTO.getTransportType());
        tour.setRouteInfo(requestDTO.getRouteInfo());

        if (requestDTO.getRouteInfo() != null) {
            tour.setTourDistance(requestDTO.getRouteInfo().getDistance());
            tour.setEstimatedTime(requestDTO.getRouteInfo().getDuration());
        }

        tour.setUser(user);

        Tour savedTour = tourRepository.save(tour);

        return mapToResponseDTO(savedTour);
    }

    public TourResponseDTO updateTour(UUID id, TourUpdateRequestDTO dto, String username) {

        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));

        // Sicherheit: gehört die Tour dem User?
        if (!tour.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        // Felder updaten
        tour.setTourName(dto.getTourName());
        tour.setDescription(dto.getDescription());
        tour.setStart(dto.getStart());
        tour.setFinish(dto.getFinish());
        tour.setTransportType(dto.getTransportType());
        tour.setRouteInfo(dto.getRouteInfo());

        if (dto.getRouteInfo() != null) {
            tour.setTourDistance(dto.getRouteInfo().getDistance());
            tour.setEstimatedTime(dto.getRouteInfo().getDuration());
        }

        Tour updatedTour = tourRepository.save(tour);

        return mapToResponseDTO(updatedTour);
    }

    public List<TourResponseDTO> getToursByUser(Users user) {
        List<Tour> tours = tourRepository.findByUser(user);
        return tours.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    public TourResponseDTO getTourById(UUID id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        return mapToResponseDTO(tour);
    }

    public void deleteTour(UUID id) {
        tourRepository.deleteById(id);
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