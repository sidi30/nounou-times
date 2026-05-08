package com.nounou.times.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Enfant extends PanacheEntity {

    @Column(nullable = false)
    private String nom;

    @Column
    private String prenom;

    @Column(nullable = false)
    private LocalDate dateNaissance;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Utilisateur parent;

    @ManyToOne
    @JoinColumn(name = "nounou_id")
    private Nounou nounou;

    @Column
    private String statut; // EN_ATTENTE, ACTIF, REFUSE, TERMINE

    @Column
    @JsonIgnore
    private String tokenInvitation;

    @Column
    private String emailParent;

    @Column
    private LocalDateTime dateInvitation;

    @Column
    private LocalDateTime dateAcceptation;

    @Column
    private LocalDateTime dateRefus;

    @Column
    private LocalDateTime dateFin;
}
