package com.example.Tourplanner.entities;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;
import lombok.*;
import java.util.UUID;

@Entity (name = "Users")
@Table (
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint (name = "username_unique", columnNames = "username"),
                @UniqueConstraint (name = "users_email_unique", columnNames = "email")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            nullable = false,
            length = 100
    )
    private String username;

    @Column(
            nullable = false,
            length = 100
    )
    private String passwordHash;

    @Column(
            nullable = false
    )
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tour> tours = new ArrayList<>();
}
