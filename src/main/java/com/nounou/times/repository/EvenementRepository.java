package com.nounou.times.repository;

import com.nounou.times.model.Evenement;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class EvenementRepository {
    @Inject
    EntityManager entityManager;

    public Evenement findById(Long id) {
        return entityManager.find(Evenement.class, id);
    }

    public List<Evenement> findAll() {
        return entityManager.createQuery("SELECT e FROM Evenement e", Evenement.class).getResultList();
    }

    @Transactional
    public void save(Evenement evenement) {
        entityManager.persist(evenement);
    }

    @Transactional
    public void update(Evenement evenement) {
        entityManager.merge(evenement);
    }

    @Transactional
    public void delete(Long id) {
        Evenement evenement = findById(id);
        if (evenement != null) {
            entityManager.remove(evenement);
        }
    }
}
