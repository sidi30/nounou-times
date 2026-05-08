package com.nounou.times.services;

import com.nounou.times.model.FicheDePaie;
import com.nounou.times.model.Garde;
import com.nounou.times.model.Nounou;
import com.nounou.times.repository.FicheDePaieRepository;
import com.nounou.times.repository.GardeRepository;
import com.nounou.times.repository.NounouRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FicheDePaieService {

    @Inject
    FicheDePaieRepository ficheDePaieRepository;

    @Inject
    NounouRepository nounouRepository;

    @Inject
    GardeRepository gardeRepository;

    public Optional<FicheDePaie> findById(Long id) {
        return ficheDePaieRepository.findByIdOptional(id);
    }

    public List<FicheDePaie> findAll() {
        return ficheDePaieRepository.listAll();
    }

    @Transactional
    public void save(FicheDePaie ficheDePaie) {
        ficheDePaieRepository.persist(ficheDePaie);
    }

    @Transactional
    public void update(FicheDePaie ficheDePaie) {
        ficheDePaieRepository.getEntityManager().merge(ficheDePaie);
    }

    @Transactional
    public void delete(Long id) {
        ficheDePaieRepository.deleteById(id);
    }

    public FicheDePaie genererOuRecuperer(Long nounouId, YearMonth periode) {
        Nounou nounou = nounouRepository.findByIdOptional(nounouId)
                .orElseThrow(() -> new IllegalArgumentException("Nounou non trouvée"));

        return ficheDePaieRepository.findByNounouAndPeriode(nounouId, periode)
                .orElseGet(() -> genererFicheDePaie(nounou, periode));
    }

    @Transactional
    public FicheDePaie genererFicheDePaie(Nounou nounou, YearMonth periode) {
        LocalDate debut = periode.atDay(1);
        LocalDate fin = periode.atEndOfMonth();

        List<Garde> gardes = gardeRepository.findByNounouAndPeriode(nounou.id, debut, fin);

        double tauxHoraire = nounou.getTauxHoraire() != null ? nounou.getTauxHoraire() : 0;
        double heuresTotales = gardes.stream()
                .mapToDouble(g -> g.getHeures() != null ? g.getHeures() : 0)
                .sum();
        double salaireBase = heuresTotales * tauxHoraire;
        double congesPayes = salaireBase * 0.10;
        double indemnitesRepas = gardes.stream().filter(Garde::isRepasInclus).count() * 5.0;
        double chargesSociales = salaireBase * 0.23;
        double salaireBrut = salaireBase + congesPayes + indemnitesRepas;
        double salaireNet = salaireBrut - chargesSociales;

        FicheDePaie fiche = new FicheDePaie();
        fiche.setNounou(nounou);
        fiche.setPeriode(periode);
        fiche.setDateGeneration(LocalDate.now());
        fiche.setHeuresTotales(heuresTotales);
        fiche.setSalaireBase(salaireBase);
        fiche.setCongesPayes(congesPayes);
        fiche.setIndemnitesRepas(indemnitesRepas);
        fiche.setChargesSociales(chargesSociales);
        fiche.setSalaireBrut(salaireBrut);
        fiche.setSalaireNet(salaireNet);

        ficheDePaieRepository.persist(fiche);
        return fiche;
    }
}
