package com.nounou.times.services;

import com.nounou.times.model.Utilisateur;
import com.nounou.times.repository.UtilisateurRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
@ApplicationScoped
public class UtilisateurService {
    @Inject
    UtilisateurRepository utilisateurRepository;

    public Utilisateur findById(Long id) {
        return utilisateurRepository.findById(id);
    }

    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    @Transactional
    public void save(Utilisateur utilisateur) {
        utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public void update(Utilisateur utilisateur) {
        utilisateurRepository.update(utilisateur);
    }

    @Transactional
    public void delete(Long id) {
        utilisateurRepository.delete(id);
    }
}
