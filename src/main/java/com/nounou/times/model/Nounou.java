package com.nounou.times.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class Nounou extends PanacheEntity {

    @Column(nullable = false)
    private String nom;

    @Column
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String motDePasse;

    @Column
    private String telephone;

    @Column
    private String adresse;

    @Column
    private LocalDate dateDebut;

    @Column
    private String pageEmploiId;

    @Column
    private Double tauxHoraire;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Utilisateur parent;

    @OneToMany(mappedBy = "nounou", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Absence> absences;

    @OneToMany(mappedBy = "nounou", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<FicheDePaie> fichesDePaie;
}
