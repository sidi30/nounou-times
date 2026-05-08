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

    @Column
    private LocalDateTime dateHeure;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String statut; // EN_ATTENTE, ACCEPTE, REFUSE, REPROGRAMME

    @Column(nullable = false)
    private String type;

    @ManyToOne
    @JoinColumn(name = "nounou_id")
    private Nounou nounou;

    @ManyToOne
    @JoinColumn(name = "enfant_id")
    private Enfant enfant;

    @Column
    private String commentaire;
}
