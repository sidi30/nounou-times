package com.nounou.times.services;

import com.nounou.times.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import io.quarkus.elytron.security.common.BcryptUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ParentService {

    @Inject
    EnfantService enfantService;

    @Inject
    NounouService nounouService;

    // 2.1. Inscription parent
    @Transactional
    public Parent signup(Parent parent) {
        if (Parent.find("email", parent.getEmail()).firstResult() != null) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }
        parent.setPassword(BcryptUtil.bcryptHash(parent.getPassword()));
        parent.persist();
        return parent;
    }

    // 2.1. Inscription parent avec Page Emploi
    @Transactional
    public Parent signupWithPageEmploi(String pageEmploiId) {
        // Logique d'inscription via Page Emploi
        // À implémenter selon l'API de Page Emploi
        throw new UnsupportedOperationException("Non implémenté");
    }

    // 2.1. Connexion parent
    public Parent login(String email, String password) {
        Parent parent = Parent.find("email", email).firstResult();
        if (parent == null || !BcryptUtil.matches(password, parent.getPassword())) {
            throw new IllegalArgumentException("Identifiants invalides");
        }

        //TODO : ajouter logique token
        return parent;
    }

    // 2.2. lister les enfants d'un parent
    public List<Enfant> getEnfants(Long parentId) {
        return enfantService.getEnfants(parentId);
    }

    // 2.3. lister les nounous d'un parent
    public List<Nounou> getNounous(Long parentId) {
        return nounouService.getNounous(parentId);
    }

    // 2.4. Accepter une nounou
    @Transactional
    public void acceptNounou(Long parentId, Long nounouId) {
        // Logique de validation de l'invitation
        Nounou nounou = Nounou.findById(nounouId);
        if (nounou == null) {
            throw new IllegalArgumentException("Nounou non trouvée");
        }
        // Implémentez la logique d'acceptation
    }

    // 2.3. Gestion des gardes 
    // 2.3.1. Rechercher la garde du jour d'un parent
    public Garde getGardeJour(Long parentId) {
        LocalDate today = LocalDate.now();
        // TODO : voir si on se base egalement sur le calendrier
        return Garde.find("date = ?1 AND enfant.parent.id = ?2", today, parentId).firstResult();
    }

    // 2.3.2. Valider le depot d'une garde
    @Transactional
    public void validerDepot(Long gardeId, Long parentId) {
        Garde garde = Garde.findById(gardeId);
        if (garde == null || !garde.getEnfant().getParent().id.equals(parentId)) {
            throw new IllegalArgumentException("Garde non trouvée ou non autorisée");
        }
        garde.setHeureDepotEffectif(LocalDateTime.now());
        garde.setDepotValide(true);
        garde.setStatus("EN_COURS");
    }

    // 2.3.3. Valider la reprise d'une garde
    @Transactional
    public void validerReprise(Long gardeId, Long parentId) {
        Garde garde = Garde.findById(gardeId);
        if (garde == null || !garde.getEnfant().getParent().id.equals(parentId)) {
            throw new IllegalArgumentException("Garde non trouvée ou non autorisée");
        }
        garde.setHeureRepriseEffective(LocalDateTime.now());
        garde.setRepriseValidee(true);
        garde.setStatus("TERMINE");
    }

    @Transactional
    public Garde declarerImprevu(Garde imprevu, Long parentId) {
        if (!imprevu.getEnfant().getParent().id.equals(parentId)) {
            throw new IllegalArgumentException("Non autorisé");
        }
        imprevu.setEstImprevu(true);
        imprevu.setStatus("PLANIFIE");
        imprevu.persist();
        return imprevu;
    }

    // 2.4. Gestion des absences et congés
    public List<Absence> getAbsences(Long parentId) {
        return Absence.list("parent.id", parentId);
    }

    @Transactional
    public Absence declarerAbsence(Absence absence, Long parentId) {
        if (!absence.getParent().id.equals(parentId)) {
            throw new IllegalArgumentException("Non autorisé");
        }
        absence.setStatut("PENDING");
        absence.persist();
        return absence;
    }

    @Transactional
    public Conge demanderConge(Conge conge, Long parentId) {
        if (!conge.getParent().id.equals(parentId)) {
            throw new IllegalArgumentException("Non autorisé");
        }
        conge.setStatut("PENDING");
        conge.persist();
        return conge;
    }

    // 2.5. Gestion des rapports et réclamations
    public List<RapportMensuel> getRapports(Long parentId) {
        return RapportMensuel.list("parent.id", parentId);
    }

    public byte[] downloadRapport(Long rapportId, Long parentId) {
        RapportMensuel rapport = RapportMensuel.findById(rapportId);
        if (rapport == null || !rapport.getParent().id.equals(parentId)) {
            throw new IllegalArgumentException("Rapport non trouvé ou non autorisé");
        }
        // Implémentez la logique de téléchargement
        throw new UnsupportedOperationException("Non implémenté");
    }

    @Transactional
    public Reclamation reclamerHeures(Reclamation reclamation, Long parentId) {
        if (!reclamation.getParent().id.equals(parentId)) {
            throw new IllegalArgumentException("Non autorisé");
        }
        reclamation.setDateReclamation(LocalDateTime.now());
        reclamation.setStatut("PENDING");
        reclamation.persist();
        return reclamation;
    }
}
