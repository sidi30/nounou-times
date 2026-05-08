package com.nounou.times.services;

import com.nounou.times.dto.AbsenceRequest;
import com.nounou.times.model.Absence;
import com.nounou.times.model.Enfant;
import com.nounou.times.model.Nounou;
import com.nounou.times.repository.AbsenceRepository;
import com.nounou.times.repository.EnfantRepository;
import com.nounou.times.repository.NounouRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AbsenceService {

    @Inject
    AbsenceRepository absenceRepository;

    @Inject
    NounouRepository nounouRepository;

    @Inject
    EnfantRepository enfantRepository;

    public Optional<Absence> findById(Long id) {
        return absenceRepository.findByIdOptional(id);
    }

    public List<Absence> findAll() {
        return absenceRepository.listAll();
    }

    public List<Absence> findByNounou(Long nounouId, LocalDate debut, LocalDate fin) {
        return absenceRepository.findByNounouAndPeriode(nounouId, debut, fin);
    }

    @Transactional
    public void save(Absence absence) {
        absenceRepository.persist(absence);
    }

    @Transactional
    public void update(Absence absence) {
        absenceRepository.getEntityManager().merge(absence);
    }

    @Transactional
    public void delete(Long id) {
        absenceRepository.deleteById(id);
    }

    @Transactional
    public Absence declarer(Long nounouId, AbsenceRequest request) {
        Nounou nounou = nounouRepository.findByIdOptional(nounouId)
                .orElseThrow(() -> new IllegalArgumentException("Nounou non trouvée"));

        if (request.getDateDebut().isAfter(request.getDateFin())) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }

        Enfant enfant = null;
        if (request.getEnfantId() != null) {
            enfant = enfantRepository.findByIdOptional(request.getEnfantId())
                    .orElseThrow(() -> new IllegalArgumentException("Enfant non trouvé"));
            if (enfant.getNounou() == null || !enfant.getNounou().id.equals(nounouId)) {
                throw new IllegalArgumentException("Cet enfant n'est pas associé à cette nounou");
            }
            if (absenceRepository.existsByEnfantAndPeriode(request.getEnfantId(), request.getDateDebut(), request.getDateFin())) {
                throw new IllegalArgumentException("Une absence existe déjà pour cet enfant sur cette période");
            }
        }

        Absence absence = new Absence();
        absence.setNounou(nounou);
        absence.setEnfant(enfant);
        absence.setDateDebut(request.getDateDebut());
        absence.setDateFin(request.getDateFin());
        absence.setMotif(request.getMotif());

        absenceRepository.persist(absence);
        return absence;
    }

    @Transactional
    public void annuler(Long nounouId, Long absenceId) {
        Absence absence = absenceRepository.findByIdOptional(absenceId)
                .orElseThrow(() -> new IllegalArgumentException("Absence non trouvée"));

        verifierAppartenance(absence, nounouId);

        if (absence.getDateDebut().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Impossible d'annuler une absence passée");
        }

        absenceRepository.delete(absence);
    }

    @Transactional
    public Absence modifier(Long nounouId, Long absenceId, AbsenceRequest request) {
        Absence absence = absenceRepository.findByIdOptional(absenceId)
                .orElseThrow(() -> new IllegalArgumentException("Absence non trouvée"));

        verifierAppartenance(absence, nounouId);

        if (absence.getDateDebut().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Impossible de modifier une absence passée");
        }

        if (request.getDateDebut().isAfter(request.getDateFin())) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }

        absence.setDateDebut(request.getDateDebut());
        absence.setDateFin(request.getDateFin());
        absence.setMotif(request.getMotif());
        return absence;
    }

    private void verifierAppartenance(Absence absence, Long nounouId) {
        if (!absence.getNounou().id.equals(nounouId)) {
            throw new IllegalArgumentException("Cette absence n'appartient pas à cette nounou");
        }
    }
}
