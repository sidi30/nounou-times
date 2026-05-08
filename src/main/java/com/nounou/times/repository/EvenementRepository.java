package com.nounou.times.repository;

import com.nounou.times.model.Evenement;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class EvenementRepository implements PanacheRepository<Evenement> {

    public List<Evenement> findByNounouAndDateRange(Long nounouId, LocalDate debut, LocalDate fin) {
        if (debut != null && fin != null) {
            LocalDateTime debutDT = debut.atStartOfDay();
            LocalDateTime finDT = fin.plusDays(1).atStartOfDay();
            return find("nounou.id = ?1 and dateHeure >= ?2 and dateHeure < ?3", nounouId, debutDT, finDT).list();
        }
        return find("nounou.id", nounouId).list();
    }
}
