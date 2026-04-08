package com.example.Tourplanner.repository;

import com.example.Tourplanner.entities.Tour;
import com.example.Tourplanner.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TourRepository extends JpaRepository<Tour, UUID> {

    // Alle Touren eines bestimmten Users
    List<Tour> findByUser(Users user);

    // Optional: Suche nach Tour-Name (für Full-Text-Suche)
    List<Tour> findByTourNameContainingIgnoreCase(String tourName);

    // Optional: Suche nach Start oder Finish
    List<Tour> findByStartContainingIgnoreCaseOrFinishContainingIgnoreCase(String start, String finish);

    // Optional: Filtern nach TransportType
    List<Tour> findByTransportType(String transportType);
}