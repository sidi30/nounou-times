package com.nounou.times.services;

import com.nounou.times.model.Enfant;
import com.nounou.times.model.Nounou;
import com.nounou.times.model.Parent;
import com.nounou.times.repository.EnfantRepository;
import com.nounou.times.repository.NounouRepository;
import com.nounou.times.repository.ParentRepository;
import com.nounou.times.dto.InvitationRequest;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class EnfantService {
    @Inject
    EnfantRepository enfantRepository;

    @Inject
    NounouRepository nounouRepository;

    @Inject
    ParentRepository parentRepository;

    @Inject
    Mailer mailer;

    public List<Enfant> getEnfantsNounou(Long nounouId) {
        return enfantRepository.findByNounou(nounouId);
    }

    @Transactional
    public void envoyerInvitation(Long nounouId, InvitationRequest invitation) {
        // Vérifier que la nounou existe
        Nounou nounou = nounouRepository.findById(nounouId);
        if (nounou == null) {
            throw new IllegalArgumentException("Nounou non trouvée");
        }

        // Générer un token unique pour l'invitation
        String token = UUID.randomUUID().toString();

        // Créer l'enfant en attente
        Enfant enfant = new Enfant();
        enfant.setNom(invitation.getNomEnfant());
        enfant.setPrenom(invitation.getPrenomEnfant());
        enfant.setDateNaissance(invitation.getDateNaissance());
        enfant.setNounou(nounou);
        enfant.setStatut("EN_ATTENTE");
        enfant.setTokenInvitation(token);
        enfant.setEmailParent(invitation.getEmailParent());
        enfant.setDateInvitation(LocalDateTime.now());

        enfantRepository.save(enfant);

        // Envoyer l'email d'invitation
        envoyerEmailInvitation(invitation.getEmailParent(), nounou, enfant, token);
    }

    @Transactional
    public void accepterInvitation(String token) {
        Enfant enfant = enfantRepository.findByToken(token);
        if (enfant == null) {
            throw new IllegalArgumentException("Invitation non trouvée ou expirée");
        }

        // Vérifier que l'invitation n'est pas expirée (24h)
        if (enfant.getDateInvitation().plusHours(24).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("L'invitation a expiré");
        }

        enfant.setStatut("ACTIF");
        enfant.setTokenInvitation(null);
        enfant.setDateAcceptation(LocalDateTime.now());

        enfantRepository.update(enfant);
    }

    @Transactional
    public void refuserInvitation(String token) {
        Enfant enfant = enfantRepository.findByToken(token);
        if (enfant == null) {
            throw new IllegalArgumentException("Invitation non trouvée ou expirée");
        }

        enfant.setStatut("REFUSE");
        enfant.setTokenInvitation(null);
        enfant.setDateRefus(LocalDateTime.now());

        enfantRepository.update(enfant);
    }

    @Transactional
    public void mettreAJour(Long nounouId, Long enfantId, String nom, String prenom, LocalDateTime dateNaissance) {
        Enfant enfant = enfantRepository.findById(enfantId);
        if (enfant == null) {
            throw new IllegalArgumentException("Enfant non trouvé");
        }

        // Vérifier que l'enfant est associé à la nounou
        if (!enfant.getNounou().getId().equals(nounouId)) {
            throw new IllegalArgumentException("Cet enfant n'est pas associé à cette nounou");
        }

        enfant.setNom(nom);
        enfant.setPrenom(prenom);
        enfant.setDateNaissance(dateNaissance);

        enfantRepository.update(enfant);
    }

    @Transactional
    public void terminerGarde(Long nounouId, Long enfantId) {
        Enfant enfant = enfantRepository.findById(enfantId);
        if (enfant == null) {
            throw new IllegalArgumentException("Enfant non trouvé");
        }

        // Vérifier que l'enfant est associé à la nounou
        if (!enfant.getNounou().getId().equals(nounouId)) {
            throw new IllegalArgumentException("Cet enfant n'est pas associé à cette nounou");
        }

        enfant.setStatut("TERMINE");
        enfant.setDateFin(LocalDateTime.now());

        enfantRepository.update(enfant);
    }

    private void envoyerEmailInvitation(String emailParent, Nounou nounou, Enfant enfant, String token) {
        String contenu = String.format(
            "Bonjour,\n\n" +
            "La nounou %s %s vous invite à ajouter votre enfant %s %s sur Nounou Times.\n\n" +
            "Pour accepter l'invitation, cliquez sur ce lien :\n" +
            "https://nounou-times.com/invitations/accepter/%s\n\n" +
            "Pour refuser l'invitation, cliquez sur ce lien :\n" +
            "https://nounou-times.com/invitations/refuser/%s\n\n" +
            "Cette invitation expire dans 24 heures.\n\n" +
            "Cordialement,\n" +
            "L'équipe Nounou Times",
            nounou.getPrenom(), nounou.getNom(),
            enfant.getPrenom(), enfant.getNom(),
            token, token
        );

        mailer.send(Mail.withText(emailParent,
            "Invitation à rejoindre Nounou Times",
            contenu
        ));
    }
}