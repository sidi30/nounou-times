package com.nounou.times.services;

import com.nounou.times.model.Reclamation;
import com.nounou.times.model.Utilisateur;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class ReclamationService {

    @Transactional
    public Reclamation creerReclamation(String description, String type, Utilisateur utilisateur) {
        Reclamation reclamation = new Reclamation();
        reclamation.setDescription(description);
        reclamation.setType(type);
        reclamation.setUtilisateur(utilisateur);
        reclamation.setDateCreation(LocalDateTime.now());
        reclamation.setStatut("OUVERTE");
        reclamation.persist();
        return reclamation;
    }

    public List<Reclamation> getReclamationsUtilisateur(Long utilisateurId) {
        return Reclamation.list("utilisateur.id", utilisateurId);
    }

    @Transactional
    public void traiterReclamation(Long reclamationId, String reponse, String statut) {
        Reclamation reclamation = Reclamation.findById(reclamationId);
        if (reclamation == null) {
            throw new IllegalArgumentException("Réclamation non trouvée");
        }
        reclamation.setReponse(reponse);
        reclamation.setStatut(statut);
        reclamation.setDateTraitement(LocalDateTime.now());
        reclamation.persist();
    }

    public List<Reclamation> getAllReclamations() {
        return Reclamation.listAll();
    }
}
