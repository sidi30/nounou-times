package com.nounou.times.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Evenement extends PanacheEntity {

    @Column(nullable = false)
    private LocalDateTime dateDebut;

    @Column(nullable = false)
    private LocalDateTime dateFin;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false)
    private String commentaire;

    @Column(nullable = false)
    private String statut; // CREE, EN_ATTENTE, ACCEPTE, REFUSE

    @Column(nullable = false)
    private String type; // GARDE, ABSENCE, CONGE, AUTRE

    @ManyToOne
    @JoinColumn(name = "nounou_id", nullable = false)
    private Nounou nounou;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @Column
    private String details;

    @Column(nullable = false)
    private boolean notificationEnvoyee = false;
}