package com.nounou.times.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Absence extends PanacheEntity {

    @Column(nullable = false)
    private LocalDateTime dateDebut;

    @Column(nullable = false)
    private LocalDateTime dateFin;

    @Column(nullable = false)
    private String raison; // e.g., "Congé", "Maladie"

    @ManyToOne
    @JoinColumn(name = "nounou_id", nullable = false)
    private Nounou nounou;

    @ManyToOne
    @JoinColumn(name = "remplacant_id")
    private Nounou remplacant;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    @ManyToOne
    @JoinColumn(name = "enfant_id", nullable = false)
    private Enfant enfant;

    @Column(nullable = false)
    private String statut; // PENDING, APPROVED, REJECTED

    @Column(nullable = false)
    private boolean justificatifFourni;

    @Column
    private String commentaire;

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
