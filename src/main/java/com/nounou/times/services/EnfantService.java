package com.nounou.times.services;

import com.nounou.times.model.Enfant;
import com.nounou.times.model.Nounou;
import com.nounou.times.model.Parent;
import com.nounou.times.dto.InvitationRequest;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class EnfantService {
    @Inject
    Mailer mailer;

    // creation enfant pour un parent
    @Transactional
    public void creerEnfant(Long parentId, String nom, String prenom, LocalDate dateNaissance) {
        Enfant enfant = new Enfant();
        enfant.setNom(nom);
        enfant.setPrenom(prenom);
        enfant.setDateNaissance(dateNaissance);
        enfant.setParent(Parent.findById(parentId));
        enfant.persist();
    }

    // liste des enfants d'un parent
    public List<Enfant> getEnfants(Long parentId) {
        return Enfant.list("parent.id", parentId);
    }

    // mise à jour de l'enfant
    @Transactional
    public void mettreAJourEnfant(Long enfantId, String nom, String prenom, LocalDate dateNaissance) {
        Enfant enfant = Enfant.findById(enfantId);
        if (enfant == null) {
            throw new IllegalArgumentException("Enfant non trouvé");
        }
        enfant.setNom(nom);
        enfant.setPrenom(prenom);
        enfant.setDateNaissance(dateNaissance);
        enfant.persist();
    }

    // supression de l'enfant
    @Transactional
    public void supprimerEnfant(Long enfantId) {
        Enfant enfant = Enfant.findById(enfantId);
        if (enfant == null) {
            throw new IllegalArgumentException("Enfant non trouvé");
        }
        enfant.delete();
    }


    @Transactional
    public void ajouterEnfant(Enfant enfant) {
        enfant.persist();
    }

    // liste des enfants garder par une nounou 
    @Transactional
    public List<Enfant> getEnfantsNounou(Long nounouId) {
        return Enfant.list("nounou.id = ?1 AND statut != 'TERMINE'", nounouId);
    }




    /**
    @Transactional
    public void envoyerInvitation(Long nounouId, InvitationRequest invitation) {
        // Vérifier que la nounou existe
        Nounou nounou = Nounou.findById(nounouId);
        if (nounou == null) {
            throw new IllegalArgumentException("Nounou non trouvée");
        }

        // Vérifier si l'enfant existe déjà
        boolean enfantExists = Enfant.count("nounou.id = ?1 AND emailParent = ?2 AND nom = ?3 AND prenom = ?4 AND statut != 'TERMINE'",
            nounouId, invitation.getEmailParent(), invitation.getNomEnfant(), invitation.getPrenomEnfant()) > 0;
            
        if (enfantExists) {
            throw new IllegalArgumentException("Un enfant avec ces informations existe déjà pour cette nounou");
        }

        // Générer un token unique pour l'invitation
        String token = UUID.randomUUID().toString();

        // Créer l'enfant en attente
        Enfant enfant = new Enfant();
        enfant.setNom(invitation.getNomEnfant());
        enfant.setNom(invitation.getPrenomEnfant());
        enfant.setDateNaissance(invitation.getDateNaissance());
        enfant.setNounou(nounou);
        enfant.setStatut("EN_ATTENTE");
        enfant.setTokenInvitation(token);
        enfant.setEmailParent(invitation.getEmailParent());
        enfant.setDateInvitation(LocalDateTime.now());

        enfant.persist();

        // Envoyer l'email d'invitation
        envoyerEmailInvitation(invitation.getEmailParent(), nounou, enfant, token);
    }

    @Transactional
    public void accepterInvitation(String token) {
        Enfant enfant = Enfant.find("tokenInvitation", token).firstResult();
        if (enfant == null) {
            throw new IllegalArgumentException("Invitation non trouvée ou expirée");
        }

        // Vérifier que l'invitation n'est pas expirée (24h)
        if (enfant.getDateInvitation().plusHours(24).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("L'invitation a expiré");
        }

        // Vérifier que l'invitation n'a pas déjà été traitée
        if (!"EN_ATTENTE".equals(enfant.getStatut())) {
            throw new IllegalArgumentException("Cette invitation a déjà été traitée");
        }

        enfant.setStatut("ACTIF");
        enfant.setTokenInvitation(null);
        enfant.setDateAcceptation(LocalDateTime.now());
        enfant.persist();
    }

    @Transactional
    public void refuserInvitation(String token) {
        Enfant enfant = Enfant.find("tokenInvitation", token).firstResult();
        if (enfant == null) {
            throw new IllegalArgumentException("Invitation non trouvée ou expirée");
        }

        // Vérifier que l'invitation n'a pas déjà été traitée
        if (!"EN_ATTENTE".equals(enfant.getStatut())) {
            throw new IllegalArgumentException("Cette invitation a déjà été traitée");
        }

        enfant.setStatut("REFUSE");
        enfant.setTokenInvitation(null);
        enfant.setDateRefus(LocalDateTime.now());
        enfant.persist();
    }

    @Transactional
    public void mettreAJour(Long nounouId, Long enfantId, String nom, String prenom, LocalDateTime dateNaissance) {
        Enfant enfant = Enfant.findById(enfantId);
        if (enfant == null) {
            throw new IllegalArgumentException("Enfant non trouvé");
        }

        // Vérifier que l'enfant est associé à la nounou
        if (!enfant.getNounou().getId().equals(nounouId)) {
            throw new IllegalArgumentException("Cet enfant n'est pas associé à cette nounou");
        }

        // Vérifier que l'enfant est actif
        if (!"ACTIF".equals(enfant.getStatut())) {
            throw new IllegalArgumentException("Impossible de modifier un enfant non actif");
        }

        enfant.setNom(nom);
        enfant.setNom(prenom);
        enfant.setDateNaissance(dateNaissance);
        enfant.persist();
    }

    @Transactional
    public void terminerGarde(Long nounouId, Long enfantId) {
        Enfant enfant = Enfant.findById(enfantId);
        if (enfant == null) {
            throw new IllegalArgumentException("Enfant non trouvé");
        }

        // Vérifier que l'enfant est associé à la nounou
        if (!enfant.getNounou().getId().equals(nounouId)) {
            throw new IllegalArgumentException("Cet enfant n'est pas associé à cette nounou");
        }

        // Vérifier que l'enfant est actif
        if (!"ACTIF".equals(enfant.getStatut())) {
            throw new IllegalArgumentException("Impossible de terminer la garde d'un enfant non actif");
        }

        enfant.setStatut("TERMINE");
        enfant.setDateFin(LocalDateTime.now());
        enfant.persist();
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
    }*/
}