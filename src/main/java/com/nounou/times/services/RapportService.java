package com.nounou.times.services;

import com.nounou.times.model.RapportMensuel;
import com.nounou.times.repository.RapportRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
@ApplicationScoped
public class RapportService {
    @Inject
    RapportRepository rapportMensuelRepository;

    public RapportMensuel findById(Long id) {
        return rapportMensuelRepository.findById(id);
    }

    public List<RapportMensuel> findAll() {
        return rapportMensuelRepository.findAll();
    }

    @Transactional
    public void save(RapportMensuel rapportMensuel) {
        rapportMensuelRepository.save(rapportMensuel);
    }

    @Transactional
    public void update(RapportMensuel rapportMensuel) {
        rapportMensuelRepository.update(rapportMensuel);
    }

    @Transactional
    public void delete(Long id) {
        rapportMensuelRepository.delete(id);
    }
}
