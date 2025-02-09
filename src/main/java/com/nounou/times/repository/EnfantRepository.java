package com.nounou.times.repository;

import com.nounou.times.model.Enfant;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class EnfantRepository {
    @Inject
    EntityManager entityManager;

    public Enfant findById(Long id) {
        return entityManager.find(Enfant.class, id);
    }

    public List<Enfant> findAll() {
        return entityManager.createQuery("SELECT e FROM Enfant e", Enfant.class).getResultList();
    }

    @Transactional
    public void save(Enfant enfant) {
        entityManager.persist(enfant);
    }

    @Transactional
    public void update(Enfant enfant) {
        entityManager.merge(enfant);
    }

    @Transactional
    public void delete(Long id) {
        Enfant enfant = findById(id);
        if (enfant != null) {
            entityManager.remove(enfant);
        }
    }
}
