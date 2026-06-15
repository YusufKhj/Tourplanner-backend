package com.example.Tourplanner.services;

import com.example.Tourplanner.dto.LogCreateRequestDTO;
import com.example.Tourplanner.dto.LogResponseDTO;
import com.example.Tourplanner.dto.LogUpdateRequestDTO;
import com.example.Tourplanner.entities.Tour;
import com.example.Tourplanner.entities.TourLog;
import com.example.Tourplanner.exceptions.TourLogNotFoundException;
import com.example.Tourplanner.exceptions.TourNotFoundException;
import com.example.Tourplanner.exceptions.UnauthorizedException;
import com.example.Tourplanner.repository.TourLogRepository;
import com.example.Tourplanner.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TourLogService {

    private static final Logger log = LoggerFactory.getLogger(TourLogService.class);

    private final TourLogRepository tourLogRepository;
    private final TourRepository tourRepository;
    private final ComputedAttributeService computedAttributeService;

    public LogResponseDTO createLog(UUID tourId, LogCreateRequestDTO dto, String username) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new TourNotFoundException("Tour not found: " + tourId));

        if (!tour.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("User " + username + " not authorized to create log for tour " + tourId);
        }

        TourLog logEntry = new TourLog();
        logEntry.setDateTime(dto.getDateTime());
        logEntry.setComment(dto.getComment());
        logEntry.setDifficulty(dto.getDifficulty());
        logEntry.setTotalDistance(dto.getTotalDistance());
        logEntry.setTotalTime(dto.getTotalTime());
        logEntry.setRating(dto.getRating());
        logEntry.setTour(tour);

        TourLog saved = tourLogRepository.save(logEntry);
        computedAttributeService.recompute(tour.getId());
        log.info("TourLog created: id={}, tourId={}, user={}", saved.getId(), tourId, username);
        return mapToResponseDTO(saved);
    }

    public LogResponseDTO updateLog(UUID logId, LogUpdateRequestDTO dto, String username) {
        TourLog logEntry = tourLogRepository.findById(logId)
                .orElseThrow(() -> new TourLogNotFoundException("TourLog not found: " + logId));

        if (!logEntry.getTour().getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("User " + username + " not authorized to update log " + logId);
        }

        logEntry.setDateTime(dto.getDateTime());
        logEntry.setComment(dto.getComment());
        logEntry.setDifficulty(dto.getDifficulty());
        logEntry.setTotalDistance(dto.getTotalDistance());
        logEntry.setTotalTime(dto.getTotalTime());
        logEntry.setRating(dto.getRating());

        TourLog updated = tourLogRepository.save(logEntry);
        computedAttributeService.recompute(logEntry.getTour().getId());
        log.info("TourLog updated: id={}, user={}", logId, username);
        return mapToResponseDTO(updated);
    }

    public void deleteLog(UUID logId, String username) {
        TourLog logEntry = tourLogRepository.findById(logId)
                .orElseThrow(() -> new TourLogNotFoundException("TourLog not found: " + logId));

        if (!logEntry.getTour().getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("User " + username + " not authorized to delete log " + logId);
        }

        UUID tourId = logEntry.getTour().getId();
        tourLogRepository.delete(logEntry);
        computedAttributeService.recompute(tourId);
        log.info("TourLog deleted: id={}, user={}", logId, username);
    }

    public List<LogResponseDTO> getLogsByTour(UUID tourId, String username) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new TourNotFoundException("Tour not found: " + tourId));

        if (!tour.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("User " + username + " not authorized to view logs for tour " + tourId);
        }

        return tourLogRepository.findByTour(tour).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public LogResponseDTO getLogById(UUID logId, String username) {
        TourLog logEntry = tourLogRepository.findById(logId)
                .orElseThrow(() -> new TourLogNotFoundException("TourLog not found: " + logId));

        if (!logEntry.getTour().getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("User " + username + " not authorized to view log " + logId);
        }

        return mapToResponseDTO(logEntry);
    }

    private LogResponseDTO mapToResponseDTO(TourLog tourLog) {
        return new LogResponseDTO(
                tourLog.getId(),
                tourLog.getDateTime(),
                tourLog.getComment(),
                tourLog.getDifficulty(),
                tourLog.getTotalDistance(),
                tourLog.getTotalTime(),
                tourLog.getRating()
        );
    }
}
