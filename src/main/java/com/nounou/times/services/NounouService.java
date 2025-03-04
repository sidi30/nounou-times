package com.nounou.times.services;

import com.nounou.times.model.Nounou;
import com.nounou.times.repository.NounouRepository;
import com.nounou.times.dto.SignupRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@ApplicationScoped
public class NounouService {

    @Inject
    NounouRepository nounouRepository;

    private Set<String> activeTokens = new HashSet<>();
    private Map<String, Long> tokenToNounouId = new HashMap<>();

    public Nounou findById(Long id) {
        return nounouRepository.findById(id);
    }

    public List<Nounou> findAll() {
        return nounouRepository.findAll();
    }

    @Transactional
    public void save(Nounou nounou) {
        nounouRepository.save(nounou);
    }

    @Transactional
    public void update(Nounou nounou) {
        nounouRepository.update(nounou);
    }

    @Transactional
    public void delete(Long id) {
        // Supprimer tous les tokens associés à cette nounou
        tokenToNounouId.entrySet()
            .removeIf(entry -> entry.getValue().equals(id));
        nounouRepository.delete(id);
    }

    @Transactional
    public Nounou signup(SignupRequest request) {
        // Vérifier si l'email existe déjà
        if (nounouRepository.findByEmail(request.getEmail()) != null) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        Nounou nounou = new Nounou();
        nounou.setEmail(request.getEmail());
        nounou.setPassword(hashPassword(request.getPassword()));
        nounou.setNom(request.getNom());
        nounou.setPrenom(request.getPrenom());
        nounou.setTelephone(request.getTelephone());
        nounou.setAdresse(request.getAdresse());

        nounouRepository.save(nounou);
        return nounou;
    }

    @Transactional
    public Nounou signupWithPageEmploi(SignupRequest request) {
        if (request.getPageEmploiId() == null || request.getPageEmploiId().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant Page Emploi est requis");
        }

        // Vérifier si l'identifiant Page Emploi existe déjà
        if (nounouRepository.findByPageEmploiId(request.getPageEmploiId()) != null) {
            throw new IllegalArgumentException("Cet identifiant Page Emploi est déjà utilisé");
        }

        Nounou nounou = new Nounou();
        nounou.setEmail(request.getEmail());
        nounou.setPassword(hashPassword(request.getPassword()));
        nounou.setNom(request.getNom());
        nounou.setPrenom(request.getPrenom());
        nounou.setTelephone(request.getTelephone());
        nounou.setAdresse(request.getAdresse());
        nounou.setPageEmploiId(request.getPageEmploiId());

        nounouRepository.save(nounou);
        return nounou;
    }

    public Nounou login(String email, String password) {
        Nounou nounou = nounouRepository.findByEmail(email);
        if (nounou != null && verifyPassword(password, nounou.getPassword())) {
            return nounou;
        }
        return null;
    }

    public String generateToken(Nounou nounou) {
        String token = UUID.randomUUID().toString();
        activeTokens.add(token);
        tokenToNounouId.put(token, nounou.getId());
        return token;
    }

    public boolean logout(String token) {
        tokenToNounouId.remove(token);
        return activeTokens.remove(token);
    }

    public boolean isValidToken(String token) {
        return activeTokens.contains(token);
    }

    public Nounou findByToken(String token) {
        if (!isValidToken(token)) {
            return null;
        }
        Long nounouId = tokenToNounouId.get(token);
        if (nounouId == null) {
            return null;
        }
        return findById(nounouId);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hachage du mot de passe", e);
        }
    }

    private boolean verifyPassword(String password, String hashedPassword) {
        String hashedInput = hashPassword(password);
        return hashedInput.equals(hashedPassword);
    }
}