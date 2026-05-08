package com.nounou.times.repository;

import com.nounou.times.model.FicheDePaie;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.YearMonth;
import java.util.Optional;

@ApplicationScoped
public class FicheDePaieRepository implements PanacheRepository<FicheDePaie> {

    public Optional<FicheDePaie> findByNounouAndPeriode(Long nounouId, YearMonth periode) {
        return find("nounou.id = ?1 and periode = ?2", nounouId, periode.toString()).firstResultOptional();
    }
}
