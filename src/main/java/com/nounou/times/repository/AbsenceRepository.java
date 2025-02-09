package com.nounou.times.repository;

import com.nounou.times.model.Absence;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

import static java.util.Collections.list;

@ApplicationScoped
public class AbsenceRepository {
    @Inject
    EntityManager entityManager;

    public Absence findById(Long id) {
        return entityManager.find(Absence.class, id);
    }

    public List<Absence> findAll() {
        return entityManager.createQuery("SELECT a FROM Absence a", Absence.class).getResultList();
    }

    @Transactional
    public void save(Absence absence) {
        entityManager.persist(absence);
    }

    @Transactional
    public void update(Absence absence) {
        entityManager.merge(absence);
    }

    @Transactional
    public void delete(Long id) {
        Absence absence = findById(id);
        if (absence != null) {
            entityManager.remove(absence);
        }
    }
}
