package com.nounou.times.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Data;
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

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private LocalDate dateNaissance;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false) // TODO a verifier
    private Utilisateur parent;

    @Column(nullable = false)
    private String statut;

    @Column(nullable = false)
    private String tokenInvitation;

    @Column(nullable = false)
    private LocalDateTime dateRefus;

    @Column(nullable = false)
    private LocalDateTime dateAcceptation;

}