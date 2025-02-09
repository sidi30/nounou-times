package com.nounou.times.repository;

import com.nounou.times.model.Garde;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class GardeRepository {
    @Inject
    EntityManager entityManager;

    public Garde findById(Long id) {
        return entityManager.find(Garde.class, id);
    }

    public List<Garde> findAll() {
        return entityManager.createQuery("SELECT g FROM Garde g", Garde.class).getResultList();
    }

    @Transactional
    public void save(Garde garde) {
        entityManager.persist(garde);
    }

    @Transactional
    public void update(Garde garde) {
        entityManager.merge(garde);
    }

    @Transactional
    public void delete(Long id) {
        Garde garde = findById(id);
        if (garde != null) {
            entityManager.remove(garde);
        }
    }
}