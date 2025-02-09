package com.nounou.times.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Evenement extends PanacheEntity {


    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String status; // e.g., "En attente", "Accepté", "Refusé"

    @Column(nullable = false)
    private String type; // e.g., "Heures supplémentaires", "Retard", "Demande de modification"

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @Column
    private String details; // Additional details about the event
}