package com.example.Tourplanner.services;

import com.example.Tourplanner.dto.LogCreateRequestDTO;
import com.example.Tourplanner.dto.TourCreateRequestDTO;
import com.example.Tourplanner.entities.Tour;
import com.example.Tourplanner.entities.Users;
import com.example.Tourplanner.exceptions.UserNotFoundException;
import com.example.Tourplanner.repository.TourRepository;
import com.example.Tourplanner.repository.UsersRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImportExportService {

    private static final Logger log = LoggerFactory.getLogger(ImportExportService.class);

    private final TourRepository tourRepository;
    private final TourLogService tourLogService;
    private final TourService tourService;
    private final UsersRepository usersRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public String exportTours(String username) {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        List<Tour> tours = tourRepository.findByUser(user);

        var exportData = tours.stream().map(tour -> Map.of(
                "tourName", tour.getTourName(),
                "description", tour.getDescription(),
                "start", tour.getStart(),
                "finish", tour.getFinish(),
                "transportType", tour.getTransportType(),
                "logs", tourLogService.getLogsByTour(tour.getId(), username)
        )).toList();

        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(exportData);
            log.info("Export completed: user={}, tours={}", username, tours.size());
            return json;
        } catch (Exception e) {
            log.error("Failed to export tours for user={}", username, e);
            throw new RuntimeException("Failed to export tours", e);
        }
    }

    public int importTours(String json, String username) {
        try {
            List<Map<String, Object>> importData = mapper.readValue(json, new TypeReference<>() {});

            for (Map<String, Object> entry : importData) {
                TourCreateRequestDTO tourDTO = new TourCreateRequestDTO();
                tourDTO.setTourName((String) entry.get("tourName"));
                tourDTO.setDescription((String) entry.get("description"));
                tourDTO.setStart((String) entry.get("start"));
                tourDTO.setFinish((String) entry.get("finish"));
                tourDTO.setTransportType((String) entry.get("transportType"));

                var tourResponse = tourService.createTour(tourDTO, username);

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> logs = (List<Map<String, Object>>) entry.get("logs");
                if (logs != null) {
                    for (Map<String, Object> logEntry : logs) {
                        LogCreateRequestDTO logDTO = new LogCreateRequestDTO();
                        logDTO.setComment((String) logEntry.get("comment"));
                        logDTO.setDifficulty((int) logEntry.get("difficulty"));
                        logDTO.setTotalDistance((double) logEntry.get("totalDistance"));
                        logDTO.setTotalTime((int) logEntry.get("totalTime"));
                        logDTO.setRating((int) logEntry.get("rating"));
                        logDTO.setTourId(tourResponse.getId().toString());

                        tourLogService.createLog(tourResponse.getId(), logDTO, username);
                    }
                }
            }

            log.info("Import completed: user={}, tours={}", username, importData.size());
            return importData.size();
        } catch (Exception e) {
            log.error("Failed to import tours for user={}", username, e);
            throw new RuntimeException("Failed to import tours", e);
        }
    }
}
