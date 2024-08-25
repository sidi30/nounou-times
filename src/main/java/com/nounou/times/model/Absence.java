package com.nounou.times.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @ManyToOne
    @JoinColumn(name = "nounou_id", nullable = false)
    private User nounou;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String reason; // E.g., "Holiday", "Sick", etc.

    // Getters and Setters
}
