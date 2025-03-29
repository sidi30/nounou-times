package com.nounou.times.services;

import com.nounou.times.model.Conge;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class CongeService implements PanacheRepository<Conge> {

    @Transactional
    public List<Conge> getConges(Long userId) {
        return list("userId", userId);
    }

    @Transactional
    public Conge demander(Long userId, LocalDateTime debut, LocalDateTime fin, String type, String motif) {
        // Validation des dates
        if (debut == null || fin == null || debut.isAfter(fin)) {
            throw new IllegalArgumentException("Les dates de congé sont invalides");
        }

        // Vérification des chevauchements
        boolean chevauchement = find("userId = ?1 AND statut != 'ANNULE' AND debut <= ?3 AND fin >= ?2",
                userId, debut, fin).firstResultOptional().isPresent();
        if (chevauchement) {
            throw new IllegalArgumentException("Il existe déjà un congé sur cette période");
        }

        Conge conge = new Conge();
        conge.setDebut(debut);
        conge.setFin(fin);
        conge.setType(type);
        conge.setMotif(motif);
        conge.setStatut("EN_ATTENTE");
        conge.setDateCreation(LocalDateTime.now());

        persist(conge);
        return conge;
    }

    @Transactional
    public Conge modifier(Long userId, Long congeId, LocalDateTime debut, LocalDateTime fin, String motif) {
        Conge conge = find("id = ?1 AND userId = ?2", congeId, userId).firstResultOptional()
                .orElseThrow(() -> new IllegalArgumentException("Congé non trouvé"));

        if (!"EN_ATTENTE".equals(conge.getStatut())) {
            throw new IllegalArgumentException("Ce congé ne peut plus être modifié");
        }

        // Validation des dates
        if (debut != null && fin != null && debut.isAfter(fin)) {
            throw new IllegalArgumentException("Les dates de congé sont invalides");
        }

        // Vérification des chevauchements
        boolean chevauchement = find("userId = ?1 AND id != ?2 AND statut != 'ANNULE' AND debut <= ?4 AND fin >= ?3",
                userId, congeId, debut != null ? debut : conge.getDebut(), fin != null ? fin : conge.getFin())
                .firstResultOptional().isPresent();
        if (chevauchement) {
            throw new IllegalArgumentException("Il existe déjà un congé sur cette période");
        }

        if (debut != null) conge.setDebut(debut);
        if (fin != null) conge.setFin(fin);
        if (motif != null) conge.setMotif(motif);
        conge.setDateModification(LocalDateTime.now());

        persist(conge);
        return conge;
    }

    @Transactional
    public void annuler(Long userId, Long congeId) {
        Conge conge = find("id = ?1 AND userId = ?2", congeId, userId).firstResultOptional()
                .orElseThrow(() -> new IllegalArgumentException("Congé non trouvé"));

        if (!"EN_ATTENTE".equals(conge.getStatut())) {
            throw new IllegalArgumentException("Ce congé ne peut plus être annulé");
        }

        conge.setStatut("ANNULE");
        conge.setDateModification(LocalDateTime.now());
        persist(conge);
    }

    @Transactional
    public void repondre(Long userId, Long congeId, String decision, String commentaire) {
        Conge conge = findById(congeId);
        if (conge == null) {
            throw new IllegalArgumentException("Congé non trouvé");
        }

        if (!"EN_ATTENTE".equals(conge.getStatut())) {
            throw new IllegalArgumentException("Ce congé a déjà été traité");
        }

        // Vérifier que l'utilisateur a le droit de répondre (à implémenter selon votre logique métier)
        
        if ("ACCEPTE".equals(decision)) {
            conge.setStatut("ACCEPTE");
        } else if ("REFUSE".equals(decision)) {
            conge.setStatut("REFUSE");
        } else {
            throw new IllegalArgumentException("Décision invalide");
        }

        conge.setCommentaire(commentaire);
        conge.setDateReponse(LocalDateTime.now());
        conge.setRepondeurId(userId);
        persist(conge);
    }
}
