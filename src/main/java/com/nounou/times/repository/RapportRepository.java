package com.nounou.times.repository;

import com.nounou.times.model.RapportMensuel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class RapportRepository implements PanacheRepository<RapportMensuel> {

    public List<RapportMensuel> findByNounouAndPeriode(Long nounouId, LocalDate debut, LocalDate fin) {
        return find("nounou.id = ?1 and dateGeneration >= ?2 and dateGeneration <= ?3",
                nounouId, debut, fin).list();
    }
}
