package com.example.Tourplanner.services;

import com.example.Tourplanner.dto.LogResponseDTO;
import com.example.Tourplanner.dto.TourResponseDTO;
import com.example.Tourplanner.entities.Users;
import com.example.Tourplanner.exceptions.UserNotFoundException;
import com.example.Tourplanner.repository.TourLogRepository;
import com.example.Tourplanner.repository.TourRepository;
import com.example.Tourplanner.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    private final TourRepository tourRepository;
    private final TourLogRepository tourLogRepository;
    private final UsersRepository usersRepository;

    public Map<String, Object> search(String query, String username) {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        var tours = tourRepository.search(query).stream()
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .map(t -> new TourResponseDTO(
                        t.getId(), t.getTourName(), t.getDescription(),
                        t.getStart(), t.getFinish(), t.getTransportType(),
                        t.getTourDistance(), t.getEstimatedTime(),
                        t.getRouteInfo(), t.getUser().getId()))
                .toList();

        var logs = tourLogRepository.searchByComment(query).stream()
                .filter(l -> l.getTour().getUser().getId().equals(user.getId()))
                .map(l -> new LogResponseDTO(
                        l.getId(), l.getDateTime(), l.getComment(),
                        l.getDifficulty(), l.getTotalDistance(),
                        l.getTotalTime(), l.getRating()))
                .toList();

        List<Map<String, Object>> computedResults = new ArrayList<>();

        for (var tour : tourRepository.findByUser(user)) {
            boolean matchesPopularity = String.valueOf(tour.getPopularity()).contains(query);
            boolean matchesChildFriendly = query.equalsIgnoreCase("child-friendly") && tour.isChildFriendly();
            boolean matchesChild = query.equalsIgnoreCase("child") && tour.isChildFriendly();
            boolean matchesPopular = query.equalsIgnoreCase("popular") && tour.getPopularity() >= 2;

            if (matchesPopularity || matchesChildFriendly || matchesChild || matchesPopular) {
                computedResults.add(Map.of(
                        "type", "computed",
                        "tourId", tour.getId(),
                        "tourName", tour.getTourName(),
                        "popularity", tour.getPopularity(),
                        "childFriendly", tour.isChildFriendly()
                ));
            }
        }

        log.debug("Search '{}' by user={}: {} tours, {} logs, {} computed", query, username, tours.size(), logs.size(), computedResults.size());

        return Map.of(
                "tours", tours,
                "logs", logs,
                "computed", computedResults
        );
    }
}
