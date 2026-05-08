package com.nounou.times.services;

import com.nounou.times.model.Enfant;
import com.nounou.times.model.Nounou;
import com.nounou.times.repository.EnfantRepository;
import com.nounou.times.repository.NounouRepository;
import com.nounou.times.dto.InvitationRequest;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class EnfantService {

    @Inject
    EnfantRepository enfantRepository;

    @Inject
    NounouRepository nounouRepository;

    @Inject
    Mailer mailer;

    public Optional<Enfant> findById(Long id) {
        return enfantRepository.findByIdOptional(id);
    }

    public List<Enfant> findAll() {
        return enfantRepository.listAll();
    }

    public List<Enfant> findByNounou(Long nounouId) {
        return enfantRepository.findByNounou(nounouId);
    }

    @Transactional
    public void save(Enfant enfant) {
        enfantRepository.persist(enfant);
    }

    @Transactional
    public void update(Enfant enfant) {
        enfantRepository.getEntityManager().merge(enfant);
    }

    @Transactional
    public void delete(Long id) {
        enfantRepository.deleteById(id);
    }

    @Transactional
    public void envoyerInvitation(Long nounouId, InvitationRequest invitation) {
        Nounou nounou = nounouRepository.findByIdOptional(nounouId)
                .orElseThrow(() -> new IllegalArgumentException("Nounou non trouvée"));

        String token = UUID.randomUUID().toString();

        Enfant enfant = new Enfant();
        enfant.setNom(invitation.getNomEnfant());
        enfant.setPrenom(invitation.getPrenomEnfant());
        enfant.setDateNaissance(invitation.getDateNaissance());
        enfant.setNounou(nounou);
        enfant.setStatut("EN_ATTENTE");
        enfant.setTokenInvitation(token);
        enfant.setEmailParent(invitation.getEmailParent());
        enfant.setDateInvitation(LocalDateTime.now());

        enfantRepository.persist(enfant);
        envoyerEmailInvitation(invitation.getEmailParent(), nounou, enfant, token);
    }

    @Transactional
    public void accepterInvitation(String token) {
        Enfant enfant = enfantRepository.findByTokenInvitation(token)
                .orElseThrow(() -> new IllegalArgumentException("Invitation non trouvée ou expirée"));

        if (enfant.getDateInvitation().plusHours(24).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("L'invitation a expiré");
        }

        enfant.setStatut("ACTIF");
        enfant.setTokenInvitation(null);
        enfant.setDateAcceptation(LocalDateTime.now());
    }

    @Transactional
    public void refuserInvitation(String token) {
        Enfant enfant = enfantRepository.findByTokenInvitation(token)
                .orElseThrow(() -> new IllegalArgumentException("Invitation non trouvée ou expirée"));

        enfant.setStatut("REFUSE");
        enfant.setTokenInvitation(null);
        enfant.setDateRefus(LocalDateTime.now());
    }

    @Transactional
    public void terminerGarde(Long nounouId, Long enfantId) {
        Enfant enfant = enfantRepository.findByIdOptional(enfantId)
                .orElseThrow(() -> new IllegalArgumentException("Enfant non trouvé"));

        verifierAssociation(enfant, nounouId);

        enfant.setStatut("TERMINE");
        enfant.setDateFin(LocalDateTime.now());
    }

    private void verifierAssociation(Enfant enfant, Long nounouId) {
        if (enfant.getNounou() == null || !enfant.getNounou().id.equals(nounouId)) {
            throw new IllegalArgumentException("Cet enfant n'est pas associé à cette nounou");
        }
    }

    private void envoyerEmailInvitation(String emailParent, Nounou nounou, Enfant enfant, String token) {
        String contenu = String.format(
            "Bonjour,%n%n" +
            "La nounou %s %s vous invite à ajouter votre enfant %s %s sur Nounou Times.%n%n" +
            "Pour accepter l'invitation:%nhttps://nounou-times.com/invitations/accepter/%s%n%n" +
            "Pour refuser l'invitation:%nhttps://nounou-times.com/invitations/refuser/%s%n%n" +
            "Cette invitation expire dans 24 heures.%n%nCordialement,%nL'équipe Nounou Times",
            nounou.getPrenom(), nounou.getNom(),
            enfant.getPrenom(), enfant.getNom(),
            token, token
        );
        mailer.send(Mail.withText(emailParent, "Invitation à rejoindre Nounou Times", contenu));
    }
}
