package com.nounou.times.services;

import com.nounou.times.model.HeuresSupplementaires;
import com.nounou.times.repository.HeuresSupplementairesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class HeuresSupplementairesService {

    @Inject
    HeuresSupplementairesRepository heuresSupplementairesRepository;

    public Optional<HeuresSupplementaires> findById(Long id) {
        return heuresSupplementairesRepository.findByIdOptional(id);
    }

    public List<HeuresSupplementaires> findAll() {
        return heuresSupplementairesRepository.listAll();
    }

    @Transactional
    public void save(HeuresSupplementaires heures) {
        heuresSupplementairesRepository.persist(heures);
    }

    @Transactional
    public void update(HeuresSupplementaires heures) {
        heuresSupplementairesRepository.getEntityManager().merge(heures);
    }

    @Transactional
    public void delete(Long id) {
        heuresSupplementairesRepository.deleteById(id);
    }
}
