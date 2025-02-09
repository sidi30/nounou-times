package com.nounou.times.services;

import com.nounou.times.model.Enfant;
import com.nounou.times.repository.EnfantRepository;
import com.nounou.times.repository.UtilisateurRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;


@ApplicationScoped
public class EnfantService {
    @Inject
    EnfantRepository enfantRepository;

    public Enfant findById(Long id) {
        return enfantRepository.findById(id);
    }

    public List<Enfant> findAll() {
        return enfantRepository.findAll();
    }

    @Transactional
    public void save(Enfant enfant) {
        enfantRepository.save(enfant);
    }

    @Transactional
    public void update(Enfant enfant) {
        enfantRepository.update(enfant);
    }

    @Transactional
    public void delete(Long id) {
        enfantRepository.delete(id);
    }
}