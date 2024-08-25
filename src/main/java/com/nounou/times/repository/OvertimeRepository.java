package com.nounou.times.repository;

import com.nounou.times.model.Overtime;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class OvertimeRepository implements PanacheRepository<Overtime> {

    public List<Overtime> findByScheduleId(Long scheduleId) {
        return list("schedule.id", scheduleId);
    }
}