package com.nounou.times.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Garde extends PanacheEntity {

    @Column(nullable = false)
    private LocalDateTime dateDebut;

    @Column(nullable = false)
    private LocalDateTime dateFin;

    @ManyToOne
    @JoinColumn(name = "enfant_id", nullable = false)
    private Enfant enfant;

    @ManyToOne
    @JoinColumn(name = "nounou_id", nullable = false)
    private Nounou nounou;

    @Column(nullable = false)
    private String statut; // PLANIFIEE, ANNULEE, TERMINEE

    @Column
    private boolean repasInclus;

    @Column
    private Double heures;

    @Column
    private String commentaire;

    @Column
    private LocalDateTime dateTerminaison;
}
