package com.nounou.times.services;

import com.nounou.times.model.Nounou;
import com.nounou.times.repository.NounouRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class NounouService {

    @Inject
    NounouRepository nounouRepository;

    public Nounou findById(Long id) {
        return nounouRepository.findById(id);
    }

    public List<Nounou> findAll() {
        return nounouRepository.findAll();
    }

    @Transactional
    public void save(Nounou nounou) {
        nounouRepository.save(nounou);
    }

    @Transactional
    public void update(Nounou nounou) {
        nounouRepository.update(nounou);
    }

    @Transactional
    public void delete(Long id) {
        nounouRepository.delete(id);
    }
}