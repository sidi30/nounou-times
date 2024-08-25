package com.nounou.times.services;

import com.nounou.times.model.Overtime;
import com.nounou.times.model.Schedule;
import com.nounou.times.repository.OvertimeRepository;
import com.nounou.times.repository.ScheduleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class OvertimeService {

    @Inject
    OvertimeRepository overtimeRepository;

    @Inject
    ScheduleRepository scheduleRepository;

    public List<Overtime> getOvertimesBySchedule(Long scheduleId) {
        return overtimeRepository.findByScheduleId(scheduleId);
    }

    @Transactional
    public Overtime createOvertime(Long scheduleId, Overtime overtime) {
        Schedule schedule = scheduleRepository.findById(scheduleId);
        if (schedule != null) {
            overtime.setSchedule(schedule);
            overtimeRepository.persist(overtime);
            return overtime;
        }
        return null;
    }

    @Transactional
    public Overtime updateOvertime(Long id, Overtime overtime) {
        Overtime existingOvertime = overtimeRepository.findById(id);
        if (existingOvertime != null) {
            existingOvertime.setOvertimeDuration(overtime.getOvertimeDuration());
            existingOvertime.setValidated(overtime.isValidated());
            return existingOvertime;
        }
        return null;
    }

    // getOvertimesByScheduleId method
    public List<Overtime> getOvertimesByScheduleId(Long scheduleId) {
        return overtimeRepository.findByScheduleId(scheduleId);
    }

    @Transactional
    public boolean deleteOvertime(Long id) {
       return overtimeRepository.deleteById(id);
    }
}
