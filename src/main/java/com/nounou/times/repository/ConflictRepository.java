package com.nounou.times.repository;

import com.nounou.times.model.Conflict;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

import static java.util.Collections.list;

@ApplicationScoped
public class ConflictRepository implements PanacheRepository<Conflict> {

    public List<Conflict> findByScheduleId(Long scheduleId) {
        return list("schedule.id", scheduleId);
    }
}