package com.nounou.times.repository;

import com.nounou.times.model.Schedule;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.list;

@ApplicationScoped
public class ScheduleRepository implements PanacheRepository<Schedule> {

    public List<Schedule> findByChildId(Long childId) {
        return list("child.id", childId);
    }

    public List<Schedule> findByParentIdAndNounouIdAndMonth(Long parentId, Long nounouId, LocalDate month) {
        return list("parent.id = ?1 and nounou.id = ?2 and function('DATE_FORMAT', arrivalTime, '%Y-%m') = ?3", parentId, nounouId, month.toString());
    }

    //findByParentIdNounouIdAndMonth
    public List<Schedule> findByParentIdNounouIdAndMonth(Long parentId, Long nounouId, LocalDate month) {
        return list("parent.id = ?1 and nounou.id = ?2 and function('DATE_FORMAT', arrivalTime, '%Y-%m') = ?3", parentId, nounouId, month.toString());
    }
}