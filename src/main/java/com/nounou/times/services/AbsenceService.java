package com.nounou.times.services;

import com.nounou.times.model.Absence;
import com.nounou.times.model.Nounou;
import com.nounou.times.model.Enfant;
import com.nounou.times.repository.AbsenceRepository;
import com.nounou.times.repository.NounouRepository;
import com.nounou.times.repository.EnfantRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class AbsenceService {
    @Inject
    AbsenceRepository absenceRepository;

    @Inject
    NounouRepository nounouRepository;

    @Inject
    EnfantRepository enfantRepository;

    public List<Absence> getAbsences(Long nounouId, LocalDate debut, LocalDate fin) {
        return absenceRepository.findByNounouAndPeriode(nounouId, debut, fin);
    }

    @Transactional
    public Absence declarer(Long nounouId, Long enfantId, LocalDateTime debut, LocalDateTime fin, String motif) {
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

        // Vérifier qu'il n'y a pas déjà une absence pour cet enfant sur cette période
        if (absenceRepository.existsByEnfantAndPeriode(enfantId, debut, fin)) {
            throw new IllegalArgumentException("Une absence existe déjà pour cet enfant sur cette période");
        }

        // Créer l'absence
        Absence absence = new Absence();
        absence.setNounou(nounou);
        absence.setEnfant(enfant);
        absence.setDateDebut(debut);
        absence.setDateFin(fin);
        absence.setMotif(motif);
        absence.setDateDeclaration(LocalDateTime.now());

        absenceRepository.save(absence);
        return absence;
    }

    @Transactional
    public void annuler(Long nounouId, Long absenceId) {
        Absence absence = absenceRepository.findById(absenceId);
        if (absence == null) {
            throw new IllegalArgumentException("Absence non trouvée");
        }

        // Vérifier que l'absence appartient à la nounou
        if (!absence.getNounou().getId().equals(nounouId)) {
            throw new IllegalArgumentException("Cette absence n'appartient pas à cette nounou");
        }

        // Vérifier que l'absence n'est pas déjà passée
        if (absence.getDateDebut().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Impossible d'annuler une absence passée");
        }

        absenceRepository.delete(absenceId);
    }

    @Transactional
    public Absence modifier(Long nounouId, Long absenceId, LocalDateTime debut, LocalDateTime fin, String motif) {
        Absence absence = absenceRepository.findById(absenceId);
        if (absence == null) {
            throw new IllegalArgumentException("Absence non trouvée");
        }

        // Vérifier que l'absence appartient à la nounou
        if (!absence.getNounou().getId().equals(nounouId)) {
            throw new IllegalArgumentException("Cette absence n'appartient pas à cette nounou");
        }

        // Vérifier que l'absence n'est pas déjà passée
        if (absence.getDateDebut().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Impossible de modifier une absence passée");
        }

        // Vérifier que les dates sont valides
        if (debut.isAfter(fin)) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }

        // Mettre à jour l'absence
        absence.setDateDebut(debut);
        absence.setDateFin(fin);
        absence.setMotif(motif);
        absence.setDateModification(LocalDateTime.now());

        absenceRepository.update(absence);
        return absence;
    }
}