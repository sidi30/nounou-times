package com.nounou.times.services;

import com.nounou.times.model.Absence;
import com.nounou.times.model.Child;
import com.nounou.times.model.User;
import com.nounou.times.repository.AbsenceRepository;
import com.nounou.times.repository.ChildRepository;
import com.nounou.times.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class AbsenceService {

    @Inject
    AbsenceRepository absenceRepository;

    @Inject
    ChildRepository childRepository;

    @Inject
    UserRepository userRepository;

    public List<Absence> getAbsencesByChild(Long childId) {
        return absenceRepository.findByChildId(childId);
    }

    @Transactional
    public Absence createAbsence(Long childId, Long nounouId, Absence absence) {
        Child child = childRepository.findById(childId);
        User nounou = userRepository.findById(nounouId);
        if (child != null && nounou != null) {
            absence.setChild(child);
            absence.setNounou(nounou);
            absenceRepository.persist(absence);
            return absence;
        }
        return null;
    }

    @Transactional
    public Absence updateAbsence(Long id, Absence absence) {
        Absence existingAbsence = absenceRepository.findById(id);
        if (existingAbsence != null) {
            existingAbsence.setReason(absence.getReason());
            return existingAbsence;
        }
        return null;
    }

    //getAbsencesByChildId method
    public List<Absence> getAbsencesByChildId(Long childId) {
        return absenceRepository.findByChildId(childId);
    }

    @Transactional
    public boolean deleteAbsence(Long id) {
        return absenceRepository.deleteById(id);
    }
}
