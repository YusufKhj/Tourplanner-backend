package com.example.Tourplanner.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourLog {

    @Id
    @GeneratedValue
    private UUID id;

    private LocalDateTime dateTime;
    private String comment;
    private int difficulty;
    private double totalDistance;
    private int totalTime;
    private int rating;

    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;
}