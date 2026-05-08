package com.nounou.times.services;

import com.nounou.times.dto.ImprevisRequest;
import com.nounou.times.dto.ReprogrammationRequest;
import com.nounou.times.model.Enfant;
import com.nounou.times.model.Evenement;
import com.nounou.times.model.Nounou;
import com.nounou.times.repository.EnfantRepository;
import com.nounou.times.repository.EvenementRepository;
import com.nounou.times.repository.NounouRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EvenementService {

    @Inject
    EvenementRepository evenementRepository;

    @Inject
    NounouRepository nounouRepository;

    @Inject
    EnfantRepository enfantRepository;

    public Optional<Evenement> findById(Long id) {
        return evenementRepository.findByIdOptional(id);
    }

    public List<Evenement> findAll() {
        return evenementRepository.listAll();
    }

    public List<Evenement> findByNounou(Long nounouId, LocalDate debut, LocalDate fin) {
        return evenementRepository.findByNounouAndDateRange(nounouId, debut, fin);
    }

    @Transactional
    public void save(Evenement evenement) {
        evenementRepository.persist(evenement);
    }

    @Transactional
    public void update(Evenement evenement) {
        evenementRepository.getEntityManager().merge(evenement);
    }

    @Transactional
    public void delete(Long id) {
        evenementRepository.deleteById(id);
    }

    @Transactional
    public Evenement creerImprevu(Long nounouId, ImprevisRequest request) {
        Nounou nounou = nounouRepository.findByIdOptional(nounouId)
                .orElseThrow(() -> new IllegalArgumentException("Nounou non trouvée"));

        Enfant enfant = null;
        if (request.getEnfantId() != null) {
            enfant = enfantRepository.findByIdOptional(request.getEnfantId())
                    .orElseThrow(() -> new IllegalArgumentException("Enfant non trouvé"));
            if (enfant.getNounou() == null || !enfant.getNounou().id.equals(nounouId)) {
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

        evenementRepository.persist(evenement);
        return evenement;
    }

    @Transactional
    public void accepterEvenement(Long nounouId, Long evenementId) {
        Evenement evenement = verifierEvenement(nounouId, evenementId);
        evenement.setStatut("ACCEPTE");
    }

    @Transactional
    public void refuserEvenement(Long nounouId, Long evenementId) {
        Evenement evenement = verifierEvenement(nounouId, evenementId);
        evenement.setStatut("REFUSE");
    }

    @Transactional
    public void reprogrammerEvenement(Long nounouId, Long evenementId, ReprogrammationRequest request) {
        Evenement evenement = verifierEvenement(nounouId, evenementId);
        evenement.setDateHeure(request.getNouvelleDate());
        evenement.setCommentaire(request.getRaison());
        evenement.setStatut("REPROGRAMME");
    }

    private Evenement verifierEvenement(Long nounouId, Long evenementId) {
        Evenement evenement = evenementRepository.findByIdOptional(evenementId)
                .orElseThrow(() -> new IllegalArgumentException("Événement non trouvé"));
        if (evenement.getNounou() == null || !evenement.getNounou().id.equals(nounouId)) {
            throw new IllegalArgumentException("Cet événement n'appartient pas à cette nounou");
        }
        return evenement;
    }
}
