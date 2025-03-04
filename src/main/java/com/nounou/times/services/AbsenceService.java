package com.nounou.times.services;

import com.nounou.times.model.Absence;
import com.nounou.times.repository.AbsenceRepository;
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
        return absenceRepository.findAll().stream().toList();
    }

    @Transactional
    public void save(Absence absence) {
        Absence.persist(absence);
    }

    @Transactional
    public void update(Absence absence) {
        absenceRepository.persist(absence);
    }

    // delete absence
    @Transactional
    public void delete(String id) {
        absenceRepository.delete(id);
    }

}