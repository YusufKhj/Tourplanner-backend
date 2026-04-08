package com.example.Tourplanner.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteInfo {

    // Koordinaten Punkte
    private List<Coordinate> coordinates;

    // Distanz in km
    private double distance;

    // Dauer in Minuten
    private double duration;
}