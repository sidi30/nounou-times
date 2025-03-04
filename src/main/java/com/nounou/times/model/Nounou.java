package com.nounou.times.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class Nounou extends PanacheEntity {

    @Id
    @GeneratedValue
    private Long id;
    
    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private Utilisateur parent;

    @OneToMany(mappedBy = "nounou", cascade = CascadeType.ALL)
    private List<Absence> absences;

    @OneToMany(mappedBy = "nounou", cascade = CascadeType.ALL)
    private List<FicheDePaie> fichesDePaie;
}