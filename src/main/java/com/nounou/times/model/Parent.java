package com.nounou.times.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
public class Parent extends PanacheEntity {
    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String telephone;
    private String adresse;

    @OneToMany(mappedBy = "parent")
    private List<Enfant> enfants;

    @OneToMany(mappedBy = "parent")
    private List<Absence> absences;

    @OneToMany(mappedBy = "parent")
    private List<Conge> conges;
}
