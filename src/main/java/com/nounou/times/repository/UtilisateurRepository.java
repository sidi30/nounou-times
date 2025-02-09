package com.nounou.times.repository;

import com.nounou.times.model.Absence;
import com.nounou.times.model.Utilisateur;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
@ApplicationScoped
public class UtilisateurRepository  implements PanacheRepository<Utilisateur> {
    public List<Utilisateur> findByNom(String nom) {
        return find("nom", nom).list();
    }

    public Utilisateur findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public Utilisateur findByEmailAndPassword(String email, String password) {
        return find("email = ?1 and password = ?2", email, password).firstResult();
    }

    public Utilisateur findByEmailAndRole(String email, String role) {
        return find("email = ?1 and role = ?2", email, role).firstResult();
    }

    public Utilisateur findByRole(String role) {
        return find("role", role).firstResult();
    }

    public List<Utilisateur> findByRoleList(String role) {
        return find("role", role).list();
    }

    public List<Utilisateur> findByRoleAndNom(String role, String nom) {
        return find("role = ?1 and nom = ?2", role, nom).list();
    }

    public List<Utilisateur> findByRoleAndPrenom(String role, String prenom) {
        return find("role = ?1 and prenom = ?2", role, prenom).list();
    }

    public List<Utilisateur> findByRoleAndNomAndPrenom(String role, String nom, String prenom) {
        return find("role = ?1 and nom = ?2 and prenom = ?3", role, nom, prenom).list();
    }

    public List<Utilisateur> findByRoleAndNomAndPrenomAndEmail(String role, String nom, String prenom, String email) {
        return find("role = ?1 and nom = ?2 and prenom = ?3 and email = ?4", role, nom, prenom, email).list();
    }

    public List<Utilisateur> findByRoleAndNomAndPrenomAndEmailAndPassword(String role, String nom, String prenom, String email, String password) {
        return find("role = ?1 and nom = ?2 and prenom = ?3 and email = ?4 and password = ?5", role, nom, prenom, email, password).list();
    }

    public List<Utilisateur> findByRoleAndNomAndPrenomAndEmailAndPasswordAndTelephone(String role, String nom, String prenom, String email, String password, String telephone) {
        return List.of();
    }
}