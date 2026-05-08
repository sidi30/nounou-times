package com.nounou.times.repository;

import com.nounou.times.model.Garde;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class GardeRepository implements PanacheRepository<Garde> {

    public List<Garde> findByNounouAndPeriode(Long nounouId, LocalDate debut, LocalDate fin) {
        LocalDateTime debutDT = debut != null ? debut.atStartOfDay() : null;
        LocalDateTime finDT = fin != null ? fin.plusDays(1).atStartOfDay() : null;
        if (debutDT != null && finDT != null) {
            return find("nounou.id = ?1 and dateDebut >= ?2 and dateDebut < ?3", nounouId, debutDT, finDT).list();
        }
        return find("nounou.id", nounouId).list();
    }

    public boolean existsByNounouAndPeriode(Long nounouId, LocalDateTime debut, LocalDateTime fin) {
        return count("nounou.id = ?1 and dateDebut < ?2 and dateFin > ?3 and statut != 'ANNULEE'",
                nounouId, fin, debut) > 0;
    }

    public boolean existsByNounouAndPeriodeExcludingGarde(Long nounouId, LocalDateTime debut, LocalDateTime fin, Long gardeId) {
        return count("nounou.id = ?1 and dateDebut < ?2 and dateFin > ?3 and statut != 'ANNULEE' and id != ?4",
                nounouId, fin, debut, gardeId) > 0;
    }
}
