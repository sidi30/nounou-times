package com.nounou.times.repository;

import com.nounou.times.model.Absence;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

import static java.util.Collections.list;

@ApplicationScoped
public class AbsenceRepository implements PanacheRepository<Absence> {

    public List<Absence> findByChildId(Long childId) {
        return list("child.id", childId);
    }
}