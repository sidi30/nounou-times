package com.nounou.times.services;

import com.nounou.times.model.Garde;
import com.nounou.times.repository.GardeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class GardeService {
    @Inject
    GardeRepository gardeRepository;

    public Garde findById(Long id) {
        return gardeRepository.findById(id);
    }

    public List<Garde> findAll() {
        return gardeRepository.findAll();
    }

    @Transactional
    public void save(Garde garde) {
        gardeRepository.save(garde);
    }

    @Transactional
    public void update(Garde garde) {
        gardeRepository.update(garde);
    }

    @Transactional
    public void delete(Long id) {
        gardeRepository.delete(id);
    }
}