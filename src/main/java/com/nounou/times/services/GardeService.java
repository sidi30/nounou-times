package com.nounou.times.services;

import com.nounou.times.model.Garde;
import com.nounou.times.model.Nounou;
import com.nounou.times.model.Enfant;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class GardeService {

    public List<Garde> getGardes(Long nounouId, LocalDate debut, LocalDate fin) {
        return Garde.find("nounou.id = ?1 AND date BETWEEN ?2 AND ?3", 
            nounouId, debut, fin).list();
    }

    @Transactional
    public Garde planifier(Long nounouId, Long enfantId, LocalDateTime debut, LocalDateTime fin, boolean repasInclus) {
        // Vérifier que la nounou existe
        Nounou nounou = Nounou.findById(nounouId);
        if (nounou == null) {
            throw new IllegalArgumentException("Nounou non trouvée");
        }

        // Vérifier que l'enfant existe et est associé à la nounou
        Enfant enfant = Enfant.findById(enfantId);
        if (enfant == null) {
            throw new IllegalArgumentException("Enfant non trouvé");
        }
        if (!enfant.getNounou().id.equals(nounouId)) {
            throw new IllegalArgumentException("Cet enfant n'est pas associé à cette nounou");
        }

        // Vérifier que les dates sont valides
        if (debut.isAfter(fin)) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }

        // Vérifier la disponibilité de la nounou sur cette période
        boolean gardeExistante = Garde.count("nounou.id = ?1 AND ((dateDebut <= ?2 AND dateFin >= ?2) OR (dateDebut <= ?3 AND dateFin >= ?3))",
            nounouId, debut, fin) > 0;
        if (gardeExistante) {
            throw new IllegalArgumentException("La nounou a déjà une garde prévue sur cette période");
        }

        // Créer la garde
        Garde garde = new Garde();
        garde.setNounou(nounou);
        garde.setEnfant(enfant);
        garde.setDate(debut.toLocalDate());
        garde.setHeureDebut(debut.toLocalTime());
        garde.setHeureFin(fin.toLocalTime());
        garde.setStatus("PLANIFIEE");
        garde.persist();
        return garde;
    }

    @Transactional
    public void annuler(Long nounouId, Long gardeId) {
        Garde garde = Garde.findById(gardeId);
        if (garde == null) {
            throw new IllegalArgumentException("Garde non trouvée");
        }

        // Vérifier que la garde appartient à la nounou
        if (!garde.getNounou().id.equals(nounouId)) {
            throw new IllegalArgumentException("Cette garde n'appartient pas à cette nounou");
        }

        // Vérifier que la garde n'est pas déjà passée
        if (garde.getDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Impossible d'annuler une garde passée");
        }

        garde.setStatus("ANNULEE");
        garde.persist();
    }

    @Transactional
    public Garde modifier(Long nounouId, Long gardeId, LocalDateTime debut, LocalDateTime fin, boolean repasInclus) {
        Garde garde = Garde.findById(gardeId);
        if (garde == null) {
            throw new IllegalArgumentException("Garde non trouvée");
        }

        // Vérifier que la garde appartient à la nounou
        if (!garde.getNounou().id.equals(nounouId)) {
            throw new IllegalArgumentException("Cette garde n'appartient pas à cette nounou");
        }

        // Vérifier que la garde n'est pas déjà passée
        if (garde.getDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Impossible de modifier une garde passée");
        }

        // Vérifier que les dates sont valides
        if (debut.isAfter(fin)) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }

        // Vérifier la disponibilité de la nounou sur cette période (en excluant la garde actuelle)
        boolean gardeExistante = Garde.count("nounou.id = ?1 AND id != ?2 AND ((dateDebut <= ?3 AND dateFin >= ?3) OR (dateDebut <= ?4 AND dateFin >= ?4))",
            nounouId, gardeId, debut, fin) > 0;
        if (gardeExistante) {
            throw new IllegalArgumentException("La nounou a déjà une garde prévue sur cette période");
        }

        // Mettre à jour la garde
        garde.setDate(debut.toLocalDate());
        garde.setHeureDebut(debut.toLocalTime());
        garde.setHeureFin(fin.toLocalTime());
        garde.persist();
        return garde;
    }

    @Transactional
    public void terminer(Long nounouId, Long gardeId, String commentaire) {
        Garde garde = Garde.findById(gardeId);
        if (garde == null) {
            throw new IllegalArgumentException("Garde non trouvée");
        }

        // Vérifier que la garde appartient à la nounou
        if (!garde.getNounou().id.equals(nounouId)) {
            throw new IllegalArgumentException("Cette garde n'appartient pas à cette nounou");
        }

        // Vérifier que la garde est bien terminée
        LocalDateTime finGarde = LocalDateTime.of(garde.getDate(), garde.getHeureFin());
        if (finGarde.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cette garde n'est pas encore terminée");
        }

        garde.setStatus("TERMINEE");
        garde.setCommentaire(commentaire);
        garde.setDateTerminaison(LocalDateTime.now());
        garde.persist();
    }

    public List<Garde> findAll() {
        return Garde.listAll();
    }

    public Garde findById(Long id) {
        return Garde.findById(id);
    }

    @Transactional
    public void save(Garde garde) {
        garde.persist();
    }

    @Transactional
    public void update(Garde garde) {
        Garde existingGarde = Garde.findById(garde.id);
        if (existingGarde != null) {
            existingGarde.setDate(garde.getDate());
            existingGarde.setHeureDebut(garde.getHeureDebut());
            existingGarde.setHeureFin(garde.getHeureFin());
            existingGarde.setStatus(garde.getStatus());
            existingGarde.setNounou(garde.getNounou());
            existingGarde.setEnfant(garde.getEnfant());
            existingGarde.persist();
        }
    }

    @Transactional
    public void delete(Long id) {
        Garde.deleteById(id);
    }
}