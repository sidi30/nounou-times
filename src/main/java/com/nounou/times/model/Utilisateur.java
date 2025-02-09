package com.nounou.times.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Utilisateur extends PanacheEntity {

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse; // Password field (hashed)

    @Column(nullable = false)
    private String civilite; // e.g., "Madame", "Monsieur"

    @Column(nullable = false)
    private String typeUtilisateur; // e.g., "Parent", "Nounou"

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<Evenement> evenements;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Enfant> enfants;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Nounou> nounous;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<RapportMensuel> rapports;
}