package com.example.Tourplanner.services;

import com.example.Tourplanner.entities.Tour;
import com.example.Tourplanner.entities.TourLog;
import com.example.Tourplanner.exceptions.TourNotFoundException;
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
public class ComputedAttributeService {

    private static final Logger log = LoggerFactory.getLogger(ComputedAttributeService.class);

    private final TourLogRepository tourLogRepository;
    private final TourRepository tourRepository;

    public void recompute(UUID tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new TourNotFoundException("Tour not found: " + tourId));

        List<TourLog> logs = tourLogRepository.findByTour(tour);

        tour.setPopularity(computePopularity(logs));
        tour.setChildFriendly(computeChildFriendliness(logs));

        tourRepository.save(tour);
        log.debug("Computed attributes updated for tourId={}: popularity={}, childFriendly={}",
                tourId, tour.getPopularity(), tour.isChildFriendly());
    }

    private int computePopularity(List<TourLog> logs) {
        int count = logs.size();
        if (count == 0) return 0;
        if (count <= 2) return 1;
        if (count <= 5) return 2;
        return 3;
    }

    private boolean computeChildFriendliness(List<TourLog> logs) {
        if (logs.isEmpty()) return false;

        double avgDifficulty = logs.stream().mapToInt(TourLog::getDifficulty).average().orElse(5);
        double avgDistance = logs.stream().mapToDouble(TourLog::getTotalDistance).average().orElse(100);
        double avgTime = logs.stream().mapToInt(TourLog::getTotalTime).average().orElse(300);

        return avgDifficulty <= 2 && avgDistance < 10 && avgTime < 60;
    }
}
