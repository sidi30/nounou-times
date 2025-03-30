package com.nounou.times.model;

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

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    @Column(nullable = false)
    private String civilite;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    @OneToMany(mappedBy = "nounou", cascade = CascadeType.ALL)
    private List<Absence> absences;

    @OneToMany(mappedBy = "nounou", cascade = CascadeType.ALL)
    private List<FicheDePaie> fichesDePaie;

    @OneToMany(mappedBy = "nounou", cascade = CascadeType.ALL)
    private List<Evenement> evenements;

    @OneToMany(mappedBy = "nounou", cascade = CascadeType.ALL)
    private List<RapportMensuel> rapports;
}