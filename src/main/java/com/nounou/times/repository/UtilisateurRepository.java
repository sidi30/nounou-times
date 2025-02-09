package com.nounou.times.repository;

import com.nounou.times.model.Utilisateur;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
@ApplicationScoped
public class UtilisateurRepository {
    @Inject
    EntityManager entityManager;

    public Utilisateur findById(Long id) {
        return entityManager.find(Utilisateur.class, id);
    }

    public List<Utilisateur> findAll() {
        return entityManager.createQuery("SELECT u FROM Utilisateur u", Utilisateur.class).getResultList();
    }

    @Transactional
    public void save(Utilisateur utilisateur) {
        entityManager.persist(utilisateur);
    }

    @Transactional
    public void update(Utilisateur utilisateur) {
        entityManager.merge(utilisateur);
    }

    @Transactional
    public void delete(Long id) {
        Utilisateur utilisateur = findById(id);
        if (utilisateur != null) {
            entityManager.remove(utilisateur);
        }
    }
}