package com.nounou.times.services;

import com.nounou.times.model.Enfant;
import com.nounou.times.model.Garde;
import com.nounou.times.model.Nounou;
import com.nounou.times.repository.EnfantRepository;
import com.nounou.times.repository.GardeRepository;
import com.nounou.times.repository.NounouRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class GardeService {

    @Inject
    GardeRepository gardeRepository;

    @Inject
    NounouRepository nounouRepository;

    @Inject
    EnfantRepository enfantRepository;

    public Optional<Garde> findById(Long id) {
        return gardeRepository.findByIdOptional(id);
    }

    public List<Garde> findAll() {
        return gardeRepository.listAll();
    }

    public List<Garde> getGardesDuJour(Long nounouId) {
        return gardeRepository.findByNounouAndPeriode(nounouId, LocalDate.now(), LocalDate.now());
    }

    public List<Garde> getHistorique(Long nounouId, LocalDate debut, LocalDate fin) {
        return gardeRepository.findByNounouAndPeriode(nounouId, debut, fin);
    }

    @Transactional
    public void save(Garde garde) {
        gardeRepository.persist(garde);
    }

    @Transactional
    public void update(Garde garde) {
        gardeRepository.getEntityManager().merge(garde);
    }

    @Transactional
    public void delete(Long id) {
        gardeRepository.deleteById(id);
    }

    @Transactional
    public Garde planifier(Long nounouId, Long enfantId, LocalDateTime debut, LocalDateTime fin, boolean repasInclus) {
        Nounou nounou = nounouRepository.findByIdOptional(nounouId)
                .orElseThrow(() -> new IllegalArgumentException("Nounou non trouvée"));

        Enfant enfant = enfantRepository.findByIdOptional(enfantId)
                .orElseThrow(() -> new IllegalArgumentException("Enfant non trouvé"));

        if (enfant.getNounou() == null || !enfant.getNounou().id.equals(nounouId)) {
            throw new IllegalArgumentException("Cet enfant n'est pas associé à cette nounou");
        }

        if (debut.isAfter(fin)) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }

        if (gardeRepository.existsByNounouAndPeriode(nounouId, debut, fin)) {
            throw new IllegalArgumentException("La nounou a déjà une garde prévue sur cette période");
        }

        Garde garde = new Garde();
        garde.setNounou(nounou);
        garde.setEnfant(enfant);
        garde.setDateDebut(debut);
        garde.setDateFin(fin);
        garde.setRepasInclus(repasInclus);
        garde.setStatut("PLANIFIEE");
        garde.setHeures(calculerHeures(debut, fin));

        gardeRepository.persist(garde);
        return garde;
    }

    @Transactional
    public void annuler(Long nounouId, Long gardeId) {
        Garde garde = gardeRepository.findByIdOptional(gardeId)
                .orElseThrow(() -> new IllegalArgumentException("Garde non trouvée"));

        verifierAppartenance(garde, nounouId);

        if (garde.getDateDebut().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Impossible d'annuler une garde passée");
        }

        garde.setStatut("ANNULEE");
    }

    @Transactional
    public Garde modifier(Long nounouId, Long gardeId, LocalDateTime debut, LocalDateTime fin, boolean repasInclus) {
        Garde garde = gardeRepository.findByIdOptional(gardeId)
                .orElseThrow(() -> new IllegalArgumentException("Garde non trouvée"));

        verifierAppartenance(garde, nounouId);

        if (garde.getDateDebut().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Impossible de modifier une garde passée");
        }

        if (debut.isAfter(fin)) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }

        if (gardeRepository.existsByNounouAndPeriodeExcludingGarde(nounouId, debut, fin, gardeId)) {
            throw new IllegalArgumentException("La nounou a déjà une garde prévue sur cette période");
        }

        garde.setDateDebut(debut);
        garde.setDateFin(fin);
        garde.setRepasInclus(repasInclus);
        garde.setHeures(calculerHeures(debut, fin));
        return garde;
    }

    @Transactional
    public void terminer(Long nounouId, Long gardeId, String commentaire) {
        Garde garde = gardeRepository.findByIdOptional(gardeId)
                .orElseThrow(() -> new IllegalArgumentException("Garde non trouvée"));

        verifierAppartenance(garde, nounouId);

        if (garde.getDateFin().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cette garde n'est pas encore terminée");
        }

        garde.setStatut("TERMINEE");
        garde.setCommentaire(commentaire);
        garde.setDateTerminaison(LocalDateTime.now());
    }

    private void verifierAppartenance(Garde garde, Long nounouId) {
        if (!garde.getNounou().id.equals(nounouId)) {
            throw new IllegalArgumentException("Cette garde n'appartient pas à cette nounou");
        }
    }

    private double calculerHeures(LocalDateTime debut, LocalDateTime fin) {
        return java.time.Duration.between(debut, fin).toMinutes() / 60.0;
    }
}
