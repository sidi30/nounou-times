package com.nounou.times.services;

import com.nounou.times.model.Evenement;
import com.nounou.times.model.Nounou;
import com.nounou.times.model.Enfant;
import com.nounou.times.repository.EvenementRepository;
import com.nounou.times.repository.NounouRepository;
import com.nounou.times.repository.EnfantRepository;
import com.nounou.times.dto.ImprevisRequest;
import com.nounou.times.dto.ReprogrammationRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class EvenementService {

    @Inject
    EvenementRepository evenementRepository;

    @Inject
    NounouRepository nounouRepository;

    @Inject
    EnfantRepository enfantRepository;

    public List<Evenement> findByNounou(Long nounouId, LocalDate debut, LocalDate fin) {
        return evenementRepository.findByNounouAndDateRange(nounouId, debut, fin);
    }

    @Transactional
    public Evenement creerImprevu(Long nounouId, ImprevisRequest request) {
        Nounou nounou = nounouRepository.findById(nounouId);
        if (nounou == null) {
            throw new IllegalArgumentException("Nounou non trouvée");
        }

        Enfant enfant = null;
        if (request.getEnfantId() != null) {
            enfant = enfantRepository.findById(request.getEnfantId());
            if (enfant == null) {
                throw new IllegalArgumentException("Enfant non trouvé");
            }
            // Vérifier que l'enfant est bien associé à la nounou
            if (!enfant.getNounou().getId().equals(nounouId)) {
                throw new IllegalArgumentException("Cet enfant n'est pas associé à cette nounou");
            }
        }

        Evenement evenement = new Evenement();
        evenement.setNounou(nounou);
        evenement.setEnfant(enfant);
        evenement.setDescription(request.getDescription());
        evenement.setDateHeure(request.getDateHeure());
        evenement.setType(request.getType());
        evenement.setStatut("EN_ATTENTE");

        evenementRepository.save(evenement);
        return evenement;
    }

    @Transactional
    public void accepterEvenement(Long nounouId, Long evenementId) {
        Evenement evenement = verifierEvenement(nounouId, evenementId);
        evenement.setStatut("ACCEPTE");
        evenementRepository.update(evenement);
    }

    @Transactional
    public void refuserEvenement(Long nounouId, Long evenementId) {
        Evenement evenement = verifierEvenement(nounouId, evenementId);
        evenement.setStatut("REFUSE");
        evenementRepository.update(evenement);
    }

    @Transactional
    public void reprogrammerEvenement(Long nounouId, Long evenementId, ReprogrammationRequest request) {
        Evenement evenement = verifierEvenement(nounouId, evenementId);
        evenement.setDateHeure(request.getNouvelleDate());
        evenement.setCommentaire(request.getRaison());
        evenement.setStatut("REPROGRAMME");
        evenementRepository.update(evenement);
    }

    private Evenement verifierEvenement(Long nounouId, Long evenementId) {
        Evenement evenement = evenementRepository.findById(evenementId);
        if (evenement == null) {
            throw new IllegalArgumentException("Événement non trouvé");
        }
        if (!evenement.getNounou().getId().equals(nounouId)) {
            throw new IllegalArgumentException("Cet événement n'appartient pas à cette nounou");
        }
        return evenement;
    }
}