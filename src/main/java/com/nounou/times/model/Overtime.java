package com.nounou.times.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Data
public class Overtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(nullable = false)
    private Duration overtimeDuration;

    @Column(nullable = false)
    private boolean validated; // indicates if the overtime has been validated

    @Column(nullable = false)
    private String createdDate;

    @Column(nullable = false)
    private String updatedDate;

    @PrePersist
    public void prePersist() {
        createdDate = updatedDate = String.valueOf(System.currentTimeMillis());
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = String.valueOf(System.currentTimeMillis());
    }
}
