package com.nounou.times.services;

import com.nounou.times.model.Garde;
import com.nounou.times.model.Nounou;
import com.nounou.times.model.Enfant;
import com.nounou.times.repository.GardeRepository;
import com.nounou.times.repository.NounouRepository;
import com.nounou.times.repository.EnfantRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class GardeService {
    @Inject
    GardeRepository gardeRepository;

    @Inject
    NounouRepository nounouRepository;

    @Inject
    EnfantRepository enfantRepository;

    public List<Garde> getGardes(Long nounouId, LocalDate debut, LocalDate fin) {
        return gardeRepository.findByNounouAndPeriode(nounouId, debut, fin);
    }

    @Transactional
    public Garde planifier(Long nounouId, Long enfantId, LocalDateTime debut, LocalDateTime fin, boolean repasInclus) {
        // Vérifier que la nounou existe
        Nounou nounou = nounouRepository.findById(nounouId);
        if (nounou == null) {
            throw new IllegalArgumentException("Nounou non trouvée");
        }

        // Vérifier que l'enfant existe et est associé à la nounou
        Enfant enfant = enfantRepository.findById(enfantId);
        if (enfant == null) {
            throw new IllegalArgumentException("Enfant non trouvé");
        }
        if (!enfant.getNounou().getId().equals(nounouId)) {
            throw new IllegalArgumentException("Cet enfant n'est pas associé à cette nounou");
        }

        // Vérifier que les dates sont valides
        if (debut.isAfter(fin)) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }

        // Vérifier la disponibilité de la nounou sur cette période
        if (gardeRepository.existsByNounouAndPeriode(nounouId, debut, fin)) {
            throw new IllegalArgumentException("La nounou a déjà une garde prévue sur cette période");
        }

        // Créer la garde
        Garde garde = new Garde();
        garde.setNounou(nounou);
        garde.setEnfant(enfant);
        garde.setDateDebut(debut);
        garde.setDateFin(fin);
        garde.setRepasInclus(repasInclus);
        garde.setStatut("PLANIFIEE");
        garde.setHeures(calculerHeures(debut, fin));

        gardeRepository.save(garde);
        return garde;
    }

    @Transactional
    public void annuler(Long nounouId, Long gardeId) {
        Garde garde = gardeRepository.findById(gardeId);
        if (garde == null) {
            throw new IllegalArgumentException("Garde non trouvée");
        }

        // Vérifier que la garde appartient à la nounou
        if (!garde.getNounou().getId().equals(nounouId)) {
            throw new IllegalArgumentException("Cette garde n'appartient pas à cette nounou");
        }

        // Vérifier que la garde n'est pas déjà passée
        if (garde.getDateDebut().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Impossible d'annuler une garde passée");
        }

        garde.setStatut("ANNULEE");
        gardeRepository.update(garde);
    }

    @Transactional
    public Garde modifier(Long nounouId, Long gardeId, LocalDateTime debut, LocalDateTime fin, boolean repasInclus) {
        Garde garde = gardeRepository.findById(gardeId);
        if (garde == null) {
            throw new IllegalArgumentException("Garde non trouvée");
        }

        // Vérifier que la garde appartient à la nounou
        if (!garde.getNounou().getId().equals(nounouId)) {
            throw new IllegalArgumentException("Cette garde n'appartient pas à cette nounou");
        }

        // Vérifier que la garde n'est pas déjà passée
        if (garde.getDateDebut().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Impossible de modifier une garde passée");
        }

        // Vérifier que les dates sont valides
        if (debut.isAfter(fin)) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }

        // Vérifier la disponibilité de la nounou sur cette période (en excluant la garde actuelle)
        if (gardeRepository.existsByNounouAndPeriodeExcludingGarde(nounouId, debut, fin, gardeId)) {
            throw new IllegalArgumentException("La nounou a déjà une garde prévue sur cette période");
        }

        // Mettre à jour la garde
        garde.setDateDebut(debut);
        garde.setDateFin(fin);
        garde.setRepasInclus(repasInclus);
        garde.setHeures(calculerHeures(debut, fin));

        gardeRepository.update(garde);
        return garde;
    }

    @Transactional
    public void terminer(Long nounouId, Long gardeId, String commentaire) {
        Garde garde = gardeRepository.findById(gardeId);
        if (garde == null) {
            throw new IllegalArgumentException("Garde non trouvée");
        }

        // Vérifier que la garde appartient à la nounou
        if (!garde.getNounou().getId().equals(nounouId)) {
            throw new IllegalArgumentException("Cette garde n'appartient pas à cette nounou");
        }

        // Vérifier que la garde est bien terminée
        if (garde.getDateFin().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cette garde n'est pas encore terminée");
        }

        garde.setStatut("TERMINEE");
        garde.setCommentaire(commentaire);
        garde.setDateTerminaison(LocalDateTime.now());

        gardeRepository.update(garde);
    }

    private double calculerHeures(LocalDateTime debut, LocalDateTime fin) {
        // Calculer la durée en heures
        long minutes = java.time.Duration.between(debut, fin).toMinutes();
        return minutes / 60.0;
    }
}