package com.nounou.times.services;

import com.nounou.times.model.FicheDePaie;
import com.nounou.times.model.Nounou;
import com.nounou.times.model.Garde;
import com.nounou.times.repository.FicheDePaieRepository;
import com.nounou.times.repository.NounouRepository;
import com.nounou.times.repository.GardeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.YearMonth;
import java.time.LocalDate;
import java.util.List;

// Service: FicheDePaieService
@ApplicationScoped
public class FicheDePaieService {
    @Inject
    FicheDePaieRepository ficheDePaieRepository;

    @Inject
    NounouRepository nounouRepository;

    @Inject
    GardeRepository gardeRepository;

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

    public FicheDePaie genererOuRecuperer(Long nounouId, YearMonth periode) {
        Nounou nounou = nounouRepository.findById(nounouId);
        if (nounou == null) {
            throw new IllegalArgumentException("Nounou non trouvée");
        }

        // Vérifier si une fiche de paie existe déjà pour cette période
        FicheDePaie ficheExistante = ficheDePaieRepository.findByNounouAndPeriode(nounouId, periode);
        if (ficheExistante != null) {
            return ficheExistante;
        }

        // Sinon, générer une nouvelle fiche de paie
        return genererFicheDePaie(nounou, periode);
    }

    @Transactional
    public FicheDePaie genererFicheDePaie(Nounou nounou, YearMonth periode) {
        LocalDate debut = periode.atDay(1);
        LocalDate fin = periode.atEndOfMonth();

        // Récupérer toutes les gardes du mois
        List<Garde> gardes = gardeRepository.findByNounouAndPeriode(nounou.getId(), debut, fin);

        // Calculer le salaire et les différentes composantes
        double tauxHoraire = nounou.getTauxHoraire() != null ? nounou.getTauxHoraire() : 0;
        double heuresTotales = calculerHeuresTotales(gardes);
        double salaireBase = heuresTotales * tauxHoraire;
        double congesPayes = calculerCongesPayes(salaireBase);
        double indemnitesRepas = calculerIndemnitesRepas(gardes);
        double chargesSociales = calculerChargesSociales(salaireBase);
        double salaireBrut = salaireBase + congesPayes + indemnitesRepas;
        double salaireNet = salaireBrut - chargesSociales;

        // Créer la fiche de paie
        FicheDePaie ficheDePaie = new FicheDePaie();
        ficheDePaie.setNounou(nounou);
        ficheDePaie.setPeriode(periode);
        ficheDePaie.setDateGeneration(LocalDate.now());
        ficheDePaie.setHeuresTotales(heuresTotales);
        ficheDePaie.setSalaireBase(salaireBase);
        ficheDePaie.setCongesPayes(congesPayes);
        ficheDePaie.setIndemnitesRepas(indemnitesRepas);
        ficheDePaie.setChargesSociales(chargesSociales);
        ficheDePaie.setSalaireBrut(salaireBrut);
        ficheDePaie.setSalaireNet(salaireNet);

        ficheDePaieRepository.save(ficheDePaie);
        return ficheDePaie;
    }

    private double calculerHeuresTotales(List<Garde> gardes) {
        return gardes.stream()
                    .mapToDouble(garde -> garde.getHeures() != null ? garde.getHeures() : 0)
                    .sum();
    }

    private double calculerCongesPayes(double salaireBase) {
        // 10% du salaire de base selon la convention collective
        return salaireBase * 0.10;
    }

    private double calculerIndemnitesRepas(List<Garde> gardes) {
        // Montant forfaitaire par repas selon la convention collective
        double montantParRepas = 5.0; // À ajuster selon la convention
        return gardes.stream()
                    .filter(garde -> garde.isRepasInclus())
                    .count() * montantParRepas;
    }

    private double calculerChargesSociales(double salaireBrut) {
        // Taux de charges sociales selon la législation en vigueur
        return salaireBrut * 0.23; // 23% de charges sociales (à ajuster selon la législation)
    }
}
