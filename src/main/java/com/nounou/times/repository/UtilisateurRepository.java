package com.nounou.times.repository;

import com.nounou.times.model.Utilisateur;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UtilisateurRepository implements PanacheRepository<Utilisateur> {

    public Optional<Utilisateur> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
}
