package com.nounou.times.repository;

import com.nounou.times.model.Absence;
import com.nounou.times.model.HeuresSupplementaires;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

import static java.util.Collections.list;

@ApplicationScoped
public class AbsenceRepository implements PanacheRepository<Absence> {
    public List<Absence> findByDescription(String description) {
        return find("description", description).list();
    }

}
