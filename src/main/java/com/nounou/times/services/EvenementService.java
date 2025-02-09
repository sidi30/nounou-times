package com.nounou.times.services;

import com.nounou.times.model.Evenement;
import com.nounou.times.repository.EvenementRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class EvenementService {

    @Inject
    EvenementRepository evenementRepository;

    public Evenement findById(Long id) {
        return evenementRepository.findById(id);
    }

    public List<Evenement> findAll() {
        return evenementRepository.findAll();
    }

    @Transactional
    public void save(Evenement evenement) {
        evenementRepository.save(evenement);
    }

    @Transactional
    public void update(Evenement evenement) {
        evenementRepository.update(evenement);
    }

    @Transactional
    public void delete(Long id) {
        evenementRepository.delete(id);
    }
}