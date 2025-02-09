package com.nounou.times.repository;

import com.nounou.times.model.FicheDePaie;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class FicheDePaieRepository {
    @Inject
    EntityManager entityManager;

    public FicheDePaie findById(Long id) {
        return entityManager.find(FicheDePaie.class, id);
    }

    public List<FicheDePaie> findAll() {
        return entityManager.createQuery("SELECT f FROM FicheDePaie f", FicheDePaie.class).getResultList();
    }

    @Transactional
    public void save(FicheDePaie ficheDePaie) {
        entityManager.persist(ficheDePaie);
    }

    @Transactional
    public void update(FicheDePaie ficheDePaie) {
        entityManager.merge(ficheDePaie);
    }

    @Transactional
    public void delete(Long id) {
        FicheDePaie ficheDePaie = findById(id);
        if (ficheDePaie != null) {
            entityManager.remove(ficheDePaie);
        }
    }
}