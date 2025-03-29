package com.nounou.times.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
public class Garde extends PanacheEntity {

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime heureDebut;

    @Column(nullable = false)
    private LocalTime heureFin;

    private LocalDateTime heureDepotEffectif;
    private LocalDateTime heureRepriseEffective;

    @ManyToOne
    @JoinColumn(name = "enfant_id", nullable = false)
    private Enfant enfant;

    @ManyToOne
    @JoinColumn(name = "nounou_id", nullable = false)
    private Nounou nounou;

    @Column(nullable = false)
    private String status; // PLANIFIE, EN_COURS, TERMINE

    private boolean depotValide;
    private boolean repriseValidee;
    private boolean estImprevu;
    public boolean isRepasInclus() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isRepasInclus'");
    }
}