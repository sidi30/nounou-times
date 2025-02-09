package com.nounou.times.services;

import com.nounou.times.model.Absence;
import com.nounou.times.repository.AbsenceRepository;
import com.nounou.times.repository.EnfantRepository;
import com.nounou.times.repository.UtilisateurRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class AbsenceService {
    @Inject
    AbsenceRepository absenceRepository;

    public Absence findById(Long id) {
        return absenceRepository.findById(id);
    }

    public List<Absence> findAll() {
        return absenceRepository.findAll();
    }

    @Transactional
    public void save(Absence absence) {
        absenceRepository.save(absence);
    }

    @Transactional
    public void update(Absence absence) {
        absenceRepository.update(absence);
    }

    @Transactional
    public void delete(Long id) {
        absenceRepository.delete(id);
    }
}