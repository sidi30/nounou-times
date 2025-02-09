package com.nounou.times.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Nounou {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private LocalDate dateDébut;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private Utilisateur parent;

    @OneToMany(mappedBy = "nounou", cascade = CascadeType.ALL)
    private List<Absence> absences;

    @OneToMany(mappedBy = "nounou", cascade = CascadeType.ALL)
    private List<FicheDePaie> fichesDePaie;
}