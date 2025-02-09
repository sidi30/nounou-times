package com.nounou.times.repository;

import com.nounou.times.model.HeuresSupplementaires;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class HeuresSupplementairesRepository {
    @Inject
    EntityManager entityManager;

    public HeuresSupplementaires findById(Long id) {
        return entityManager.find(HeuresSupplementaires.class, id);
    }

    public List<HeuresSupplementaires> findAll() {
        return entityManager.createQuery("SELECT h FROM HeuresSupplementaires h", HeuresSupplementaires.class).getResultList();
    }

    @Transactional
    public void save(HeuresSupplementaires heuresSupplementaires) {
        entityManager.persist(heuresSupplementaires);
    }

    @Transactional
    public void update(HeuresSupplementaires heuresSupplementaires) {
        entityManager.merge(heuresSupplementaires);
    }

    @Transactional
    public void delete(Long id) {
        HeuresSupplementaires heuresSupplementaires = findById(id);
        if (heuresSupplementaires != null) {
            entityManager.remove(heuresSupplementaires);
        }
    }
}
