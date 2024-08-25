package com.nounou.times.services;

import com.nounou.times.model.Conflict;
import com.nounou.times.model.Schedule;
import com.nounou.times.repository.ConflictRepository;
import com.nounou.times.repository.ScheduleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class ConflictService {

    @Inject
    ConflictRepository conflictRepository;

    @Inject
    ScheduleRepository scheduleRepository;

    public List<Conflict> getConflictsBySchedule(Long scheduleId) {
        return conflictRepository.findByScheduleId(scheduleId);
    }

    @Transactional
    public Conflict reportConflict(Long scheduleId, Conflict conflict) {
        Schedule schedule = scheduleRepository.findById(scheduleId);
        if (schedule != null) {
            conflict.setSchedule(schedule);
            conflictRepository.persist(conflict);
            return conflict;
        }
        return null;
    }

    @Transactional
    public Conflict resolveConflict(Long id, boolean resolved) {
        Conflict conflict = conflictRepository.findById(id);
        if (conflict != null) {
            conflict.setResolved(resolved);
            return conflict;
        }
        return null;
    }

    // getConflictsByScheduleId method
    public List<Conflict> getConflictsByScheduleId(Long scheduleId) {
        return conflictRepository.findByScheduleId(scheduleId);
    }

    // createConflict method
    public Conflict createConflict(Conflict conflict) {
        conflictRepository.persist(conflict);
        return conflict;
    }

    // updateConflict method
    public Conflict updateConflict(Long id, Conflict conflict) {
        Conflict existingConflict = conflictRepository.findById(id);
        if (existingConflict != null) {
            existingConflict.setReason(conflict.getReason());
            existingConflict.setResolved(conflict.isResolved());
            return existingConflict;
        }
        return null;
    }

    @Transactional
    public boolean deleteConflict(Long id) {
       return conflictRepository.deleteById(id);
    }
}
