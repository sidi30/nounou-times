package com.nounou.times.repository;

import com.nounou.times.model.Absence;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class AbsenceRepository implements PanacheRepository<Absence> {

    public List<Absence> findByNounouAndPeriode(Long nounouId, LocalDate debut, LocalDate fin) {
        if (debut != null && fin != null) {
            return find("nounou.id = ?1 and dateDebut >= ?2 and dateFin <= ?3", nounouId, debut, fin).list();
        }
        return find("nounou.id", nounouId).list();
    }

    public boolean existsByEnfantAndPeriode(Long enfantId, LocalDate debut, LocalDate fin) {
        return count("enfant.id = ?1 and dateDebut < ?2 and dateFin > ?3", enfantId, fin, debut) > 0;
    }
}
