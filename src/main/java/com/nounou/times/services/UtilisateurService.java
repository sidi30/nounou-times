package com.nounou.times.services;

import com.nounou.times.model.Utilisateur;
import com.nounou.times.repository.UtilisateurRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class UtilisateurService {
    @Inject
    UtilisateurRepository utilisateurRepository;

    public Utilisateur findById(Long id) {
        return utilisateurRepository.findById(id);
    }

    public List<Utilisateur> findAll() {
        return utilisateurRepository.listAll();
    }

    @Transactional
    public void save(Utilisateur utilisateur) {
        utilisateurRepository.persist(utilisateur);
    }

    @Transactional
    public void update(Utilisateur utilisateur) {
        utilisateurRepository.persist(utilisateur);
    }

    @Transactional
    public void delete(Long id) {
        utilisateurRepository.deleteById(id);
    }

    public Utilisateur login(String email, String motDePasse) {
        return utilisateurRepository.findByEmailAndPassword(email, motDePasse);
    }

    // Note: Dans une vraie application, vous devriez stocker les sessions dans une base de données ou un cache
    private Set<String> activeTokens = new HashSet<>();

    public String generateToken(Utilisateur utilisateur) {
        String token = UUID.randomUUID().toString();
        activeTokens.add(token);
        return token;
    }

    public boolean logout(String token) {
        return activeTokens.remove(token);
    }

    public boolean isValidToken(String token) {
        return activeTokens.contains(token);
    }
}
