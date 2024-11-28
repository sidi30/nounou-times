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
    private String reason;

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
    }}
