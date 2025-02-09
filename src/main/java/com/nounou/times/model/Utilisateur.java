package com.nounou.times.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse; // Password field (hashed)

    @Column(nullable = false)
    private String civilité; // e.g., "Madame", "Monsieur"

    @Column(nullable = false)
    private String typeUtilisateur; // e.g., "Parent", "Nounou"

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<Evenement> événements;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Enfant> enfants;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Nounou> nounous;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<RapportMensuel> rapports;
}