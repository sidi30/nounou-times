package com.nounou.times.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Getter
@Setter
public class Nounou extends PanacheEntity{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String adresse;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private double tarifHoraire;

    @Column(nullable = false)
    private double majorationRepas;

    @Column(nullable = false)
    private double majorationKm;

    @Column(nullable = false)
    private boolean actif = true;

    @OneToMany(mappedBy = "nounou", cascade = CascadeType.ALL)
    private List<Garde> gardes;

    @OneToMany(mappedBy = "nounou", cascade = CascadeType.ALL)
    private List<Absence> absences;

    @OneToMany(mappedBy = "nounou", cascade = CascadeType.ALL)
    private List<RapportMensuel> rapports;

    @OneToMany(mappedBy = "nounou", cascade = CascadeType.ALL)
    private List<FicheDePaie> fichesDePaie;

    @OneToMany(mappedBy = "nounou", cascade = CascadeType.ALL)
    private List<Evenement> evenements;
}