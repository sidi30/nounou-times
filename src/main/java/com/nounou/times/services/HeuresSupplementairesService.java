package com.nounou.times.services;

import com.nounou.times.model.HeuresSupplementaires;
import com.nounou.times.repository.HeuresSupplementairesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class HeuresSupplementairesService  {
    @Inject
    HeuresSupplementairesRepository heuresSupplementairesRepository;

    public HeuresSupplementaires findById(Long id) {
        return heuresSupplementairesRepository.findById(id);
    }

    public List<HeuresSupplementaires> findAll() {
        return heuresSupplementairesRepository.findAll();
    }

    @Transactional
    public void save(HeuresSupplementaires heuresSupplementaires) {
        heuresSupplementairesRepository.save(heuresSupplementaires);
    }

    @Transactional
    public void update(HeuresSupplementaires heuresSupplementaires) {
        heuresSupplementairesRepository.update(heuresSupplementaires);
    }

    @Transactional
    public void delete(Long id) {
        heuresSupplementairesRepository.delete(id);
    }
}