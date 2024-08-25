package com.nounou.times.services;

import com.nounou.times.model.Child;
import com.nounou.times.model.Schedule;
import com.nounou.times.model.User;
import com.nounou.times.repository.ChildRepository;
import com.nounou.times.repository.ScheduleRepository;
import com.nounou.times.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class ScheduleService {

    @Inject
    ScheduleRepository scheduleRepository;

    @Inject
    ChildRepository childRepository;

    @Inject
    UserRepository userRepository;

    public List<Schedule> getSchedulesByChild(Long childId) {
        return scheduleRepository.findByChildId(childId);
    }

    @Transactional
    public Schedule createSchedule(Long childId, Long parentId, Long nounouId, Schedule schedule) {
        Child child = childRepository.findById(childId);
        User parent = userRepository.findById(parentId);
        User nounou = userRepository.findById(nounouId);
        if (child != null && parent != null && nounou != null) {
            schedule.setChild(child);
            schedule.setParent(parent);
            schedule.setNounou(nounou);
            scheduleRepository.persist(schedule);
            return schedule;
        }
        return null;
    }

    @Transactional
    public Schedule updateSchedule(Long id, Schedule schedule) {
        Schedule existingSchedule = scheduleRepository.findById(id);
        if (existingSchedule != null) {
            existingSchedule.setArrivalTime(schedule.getArrivalTime());
            existingSchedule.setDepartureTime(schedule.getDepartureTime());
            existingSchedule.setValidated(schedule.isValidated());
            existingSchedule.setOvertime(schedule.isOvertime());
            return existingSchedule;
        }
        return null;
    }

    /// getSchedulesByParentIdNounouIdAndMonth
    public List<Schedule> getSchedulesByParentIdNounouIdAndMonth(Long parentId, Long nounouId, LocalDate month) {
        return scheduleRepository.findByParentIdNounouIdAndMonth(parentId, nounouId, month);
    }

    // getSchedulesByChildId
    public List<Schedule> getSchedulesByChildId(Long childId) {
        return scheduleRepository.findByChildId(childId);
    }
    @Transactional
    public boolean deleteSchedule(Long id) {
       return scheduleRepository.deleteById(id);
    }
}
