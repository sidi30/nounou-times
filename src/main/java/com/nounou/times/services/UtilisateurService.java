package com.nounou.times.services;

import com.nounou.times.model.Utilisateur;
import com.nounou.times.repository.UtilisateurRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UtilisateurService {

    @Inject
    UtilisateurRepository utilisateurRepository;

    private final Map<String, Long> activeTokens = new HashMap<>();

    public Optional<Utilisateur> findById(Long id) {
        return utilisateurRepository.findByIdOptional(id);
    }

    public List<Utilisateur> findAll() {
        return utilisateurRepository.listAll();
    }

    @Transactional
    public void save(Utilisateur utilisateur) {
        utilisateur.setMotDePasse(BcryptUtil.bcryptHash(utilisateur.getMotDePasse()));
        utilisateurRepository.persist(utilisateur);
    }

    @Transactional
    public void update(Utilisateur utilisateur) {
        utilisateurRepository.getEntityManager().merge(utilisateur);
    }

    @Transactional
    public void delete(Long id) {
        activeTokens.entrySet().removeIf(e -> e.getValue().equals(id));
        utilisateurRepository.deleteById(id);
    }

    public Optional<Utilisateur> login(String email, String motDePasse) {
        return utilisateurRepository.findByEmail(email)
                .filter(u -> BcryptUtil.matches(motDePasse, u.getMotDePasse()));
    }

    public String generateToken(Utilisateur utilisateur) {
        String token = UUID.randomUUID().toString();
        activeTokens.put(token, utilisateur.id);
        return token;
    }

    public boolean logout(String token) {
        return activeTokens.remove(token) != null;
    }

    public boolean isValidToken(String token) {
        return token != null && activeTokens.containsKey(token);
    }
}
