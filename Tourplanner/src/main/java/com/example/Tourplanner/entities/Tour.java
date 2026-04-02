package com.example.Tourplanner.entities;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;
import java.util.Map;

@Entity (name = "Tour")
@Table(name = "tour")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tour {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    @Column(
        nullable = false,
        length = 200
    )
    private String tourName;

    @Column(
        nullable = false,
        length = 1000
    )    
    private String description;
    
    @Column(
        nullable = false,
        length = 100
    )   
    private String start;

    @Column(
        nullable = false,
        length = 100
    )  
    private String finish;

    @Column(
        nullable = false,
        length = 100
    )  
    private String transportType;

    @Column(
        nullable = false
    )  
    private double tourDistance;

    @Column(
        nullable = false
    )  
    private LocalDate estimatedTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

}
