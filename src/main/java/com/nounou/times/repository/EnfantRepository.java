package com.nounou.times.repository;

import com.nounou.times.model.Enfant;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EnfantRepository implements PanacheRepository<Enfant> {

    public List<Enfant> findByNounou(Long nounouId) {
        return find("nounou.id", nounouId).list();
    }

    public Optional<Enfant> findByTokenInvitation(String token) {
        return find("tokenInvitation", token).firstResultOptional();
    }
}
