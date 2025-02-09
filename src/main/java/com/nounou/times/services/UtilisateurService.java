package com.nounou.times.services;

import com.nounou.times.model.Utilisateur;
import com.nounou.times.repository.UtilisateurRepository;
import com.nounou.times.security.TokenService;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.List;

@ApplicationScoped
public class UtilisateurService {
    @Inject
    UtilisateurRepository utilisateurRepository;

    @Inject
    TokenService tokenService;

    public Utilisateur findById(Long id) {
        return utilisateurRepository.findById(id);
    }

    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.find("email", email).firstResult();
    }

    public List<Utilisateur> findAll() {
        return utilisateurRepository.listAll();
    }

    @Transactional
    public void save(Utilisateur utilisateur) {
        // Ne hasher le mot de passe que s'il ne s'agit pas d'un utilisateur Keycloak
        if (!"KEYCLOAK_USER".equals(utilisateur.getMotDePasse())) {
            utilisateur.setMotDePasse(BcryptUtil.bcryptHash(utilisateur.getMotDePasse()));
        }
        utilisateurRepository.persist(utilisateur);
    }

    @Transactional
    public void update(Utilisateur utilisateur) {
        Utilisateur existingUser = utilisateurRepository.findById(utilisateur.id);
        if (existingUser == null) {
            throw new WebApplicationException("Utilisateur non trouvé", Response.Status.NOT_FOUND);
        }

        // Ne mettre à jour le mot de passe que s'il a été modifié et qu'il ne s'agit pas d'un utilisateur Keycloak
        if (!utilisateur.getMotDePasse().equals(existingUser.getMotDePasse()) 
            && !"KEYCLOAK_USER".equals(existingUser.getMotDePasse())) {
            utilisateur.setMotDePasse(BcryptUtil.bcryptHash(utilisateur.getMotDePasse()));
        }

        utilisateurRepository.persist(utilisateur);
    }

    @Transactional
    public void delete(Long id) {
        utilisateurRepository.deleteById(id);
    }

    public String login(String email, String motDePasse) {
        Utilisateur utilisateur = utilisateurRepository.find("email", email).firstResult();
        if (utilisateur != null && BcryptUtil.matches(motDePasse, utilisateur.getMotDePasse())) {
            return tokenService.generateToken(utilisateur);
        }
        throw new WebApplicationException("Email ou mot de passe incorrect", Response.Status.UNAUTHORIZED);
    }

    public String generateSocialToken(Utilisateur utilisateur) {
        return tokenService.generateToken(utilisateur);
    }
}
