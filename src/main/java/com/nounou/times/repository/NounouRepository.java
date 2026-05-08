package com.nounou.times.repository;

import com.nounou.times.model.Nounou;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class NounouRepository implements PanacheRepository<Nounou> {

    public Optional<Nounou> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public Optional<Nounou> findByPageEmploiId(String pageEmploiId) {
        return find("pageEmploiId", pageEmploiId).firstResultOptional();
    }
}
