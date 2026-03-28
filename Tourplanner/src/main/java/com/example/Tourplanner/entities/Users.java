package com.example.Tourplanner.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity (name = "Users")
@Table (
        name = "users",
        uniqueConstraints = {
        @UniqueConstraint(
                name = "employee_users_unique",
                columnNames = "email"
        )
    }
)

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

    public Users() {
    }

    public Users(String username, String passwordHash, String email) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
