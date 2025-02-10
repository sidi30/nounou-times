package com.nounou.times.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@UserDefinition
public class Utilisateur extends PanacheEntity {

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    @Username
    private String email;

    @Column(nullable = false)
    @Password
    private String motDePasse;

    @Column(nullable = false)
    private String civilite;

    @Column(nullable = false)
    @Roles
    private String typeUtilisateur; // "PARENT" ou "NOUNOU"

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<Evenement> evenements;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Enfant> enfants;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Nounou> nounous;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<RapportMensuel> rapports;
}