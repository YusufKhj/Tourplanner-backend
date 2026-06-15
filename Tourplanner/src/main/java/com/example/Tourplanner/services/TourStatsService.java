package com.example.Tourplanner.services;

import com.example.Tourplanner.dto.TourStatsResponseDTO;
import com.example.Tourplanner.entities.Tour;
import com.example.Tourplanner.entities.TourLog;
import com.example.Tourplanner.entities.Users;
import com.example.Tourplanner.exceptions.UserNotFoundException;
import com.example.Tourplanner.repository.TourLogRepository;
import com.example.Tourplanner.repository.TourRepository;
import com.example.Tourplanner.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourStatsService {

    private static final Logger log = LoggerFactory.getLogger(TourStatsService.class);

    private final TourRepository tourRepository;
    private final TourLogRepository tourLogRepository;
    private final UsersRepository usersRepository;

    public TourStatsResponseDTO getStats(String username) {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        List<Tour> tours = tourRepository.findByUser(user);

        long totalTours = tours.size();
        double totalDistanceKm = tours.stream().mapToDouble(Tour::getTourDistance).sum();
        double averageDistanceKm = totalTours > 0 ? totalDistanceKm / totalTours : 0;

        List<TourLog> allLogs = tours.stream()
                .flatMap(t -> tourLogRepository.findByTour(t).stream())
                .toList();

        long totalLogs = allLogs.size();
        double averageRating = allLogs.stream()
                .mapToInt(TourLog::getRating)
                .average()
                .orElse(0);

        int totalDurationMinutes = allLogs.stream()
                .mapToInt(TourLog::getTotalTime)
                .sum();

        Map<String, Long> transportTypeDistribution = tours.stream()
                .collect(Collectors.groupingBy(Tour::getTransportType, Collectors.counting()));

        String mostUsedTransportType = transportTypeDistribution.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        log.info("Stats computed for user={}: {} tours, {}km total, {} logs", username, totalTours, totalDistanceKm, totalLogs);

        return new TourStatsResponseDTO(
                totalTours,
                totalDistanceKm,
                Math.round(averageDistanceKm * 100.0) / 100.0,
                Math.round(averageRating * 10.0) / 10.0,
                totalLogs,
                mostUsedTransportType,
                transportTypeDistribution,
                totalDurationMinutes
        );
    }
}
