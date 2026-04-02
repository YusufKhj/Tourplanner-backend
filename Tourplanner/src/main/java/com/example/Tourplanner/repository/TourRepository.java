package com.example.Tourplanner.repository;

import com.example.Tourplanner.entities.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TourRepository extends JpaRepository<Tour, UUID> {
}
