package com.example.Tourplanner.repository;

import com.example.Tourplanner.entities.Tour;
import com.example.Tourplanner.entities.TourLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TourLogRepository extends JpaRepository<TourLog, UUID> {
    List<TourLog> findByTour(Tour tour);

    @Query("SELECT tl FROM TourLog tl WHERE LOWER(tl.comment) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<TourLog> searchByComment(@Param("q") String q);
}