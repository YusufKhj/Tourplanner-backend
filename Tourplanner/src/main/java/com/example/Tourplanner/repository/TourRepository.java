package com.example.Tourplanner.repository;

import com.example.Tourplanner.entities.Tour;
import com.example.Tourplanner.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

public interface TourRepository extends JpaRepository<Tour, UUID> {

    List<Tour> findByUser(Users user);

    List<Tour> findByTourNameContainingIgnoreCase(String tourName);

    List<Tour> findByStartContainingIgnoreCaseOrFinishContainingIgnoreCase(String start, String finish);

    List<Tour> findByTransportType(String transportType);

    @Query("SELECT t FROM Tour t WHERE " +
           "LOWER(t.tourName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(t.start) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(t.finish) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Tour> search(@Param("q") String q);
}