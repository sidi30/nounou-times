package com.nounou.times.repository;

import com.nounou.times.model.Nounou;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class NounouRepository {

    @Inject
    EntityManager entityManager;

    public Nounou findById(Long id) {
        return entityManager.find(Nounou.class, id);
    }

    public List<Nounou> findAll() {
        return entityManager.createQuery("SELECT n FROM Nounou n", Nounou.class).getResultList();
    }

    @Transactional
    public void save(Nounou nounou) {
        entityManager.persist(nounou);
    }

    @Transactional
    public void update(Nounou nounou) {
        entityManager.merge(nounou);
    }

    @Transactional
    public void delete(Long id) {
        Nounou nounou = findById(id);
        if (nounou != null) {
            entityManager.remove(nounou);
        }
    }
}
