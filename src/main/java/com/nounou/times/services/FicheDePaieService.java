package com.nounou.times.services;

import com.nounou.times.model.FicheDePaie;
import com.nounou.times.repository.FicheDePaieRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

// Service: FicheDePaieService
@ApplicationScoped
public class FicheDePaieService {
    @Inject
    FicheDePaieRepository ficheDePaieRepository;

    public FicheDePaie findById(Long id) {
        return ficheDePaieRepository.findById(id);
    }

    public List<FicheDePaie> findAll() {
        return ficheDePaieRepository.findAll();
    }

    @Transactional
    public void save(FicheDePaie ficheDePaie) {
        ficheDePaieRepository.save(ficheDePaie);
    }

    @Transactional
    public void update(FicheDePaie ficheDePaie) {
        ficheDePaieRepository.update(ficheDePaie);
    }

    @Transactional
    public void delete(Long id) {
        ficheDePaieRepository.delete(id);
    }
}
