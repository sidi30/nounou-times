package com.nounou.times.services;

import com.nounou.times.model.Nounou;
import com.nounou.times.repository.NounouRepository;
import com.nounou.times.dto.SignupRequest;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class NounouService {

    @Inject
    NounouRepository nounouRepository;

    private final Set<String> activeTokens = new HashSet<>();
    private final Map<String, Long> tokenToNounouId = new HashMap<>();

    public Optional<Nounou> findById(Long id) {
        return nounouRepository.findByIdOptional(id);
    }

    public List<Nounou> findAll() {
        return nounouRepository.listAll();
    }

    @Transactional
    public void save(Nounou nounou) {
        nounouRepository.persist(nounou);
    }

    @Transactional
    public void update(Nounou nounou) {
        nounouRepository.getEntityManager().merge(nounou);
    }

    @Transactional
    public void delete(Long id) {
        tokenToNounouId.entrySet().removeIf(entry -> entry.getValue().equals(id));
        nounouRepository.deleteById(id);
    }

    @Transactional
    public Nounou signup(SignupRequest request) {
        if (nounouRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }
        Nounou nounou = buildFromRequest(request);
        nounouRepository.persist(nounou);
        return nounou;
    }

    @Transactional
    public Nounou signupWithPageEmploi(SignupRequest request) {
        if (request.getPageEmploiId() == null || request.getPageEmploiId().isBlank()) {
            throw new IllegalArgumentException("L'identifiant Page Emploi est requis");
        }
        if (nounouRepository.findByPageEmploiId(request.getPageEmploiId()).isPresent()) {
            throw new IllegalArgumentException("Cet identifiant Page Emploi est déjà utilisé");
        }
        Nounou nounou = buildFromRequest(request);
        nounou.setPageEmploiId(request.getPageEmploiId());
        nounouRepository.persist(nounou);
        return nounou;
    }

    public Optional<Nounou> login(String email, String password) {
        return nounouRepository.findByEmail(email)
                .filter(n -> BcryptUtil.matches(password, n.getMotDePasse()));
    }

    public String generateToken(Nounou nounou) {
        String token = UUID.randomUUID().toString();
        activeTokens.add(token);
        tokenToNounouId.put(token, nounou.id);
        return token;
    }

    public boolean logout(String token) {
        tokenToNounouId.remove(token);
        return activeTokens.remove(token);
    }

    public boolean isValidToken(String token) {
        return token != null && activeTokens.contains(token);
    }

    public Optional<Nounou> findByToken(String token) {
        if (!isValidToken(token)) return Optional.empty();
        Long id = tokenToNounouId.get(token);
        if (id == null) return Optional.empty();
        return findById(id);
    }

    private Nounou buildFromRequest(SignupRequest request) {
        Nounou nounou = new Nounou();
        nounou.setEmail(request.getEmail());
        nounou.setMotDePasse(BcryptUtil.bcryptHash(request.getPassword()));
        nounou.setNom(request.getNom());
        nounou.setPrenom(request.getPrenom());
        nounou.setTelephone(request.getTelephone());
        nounou.setAdresse(request.getAdresse());
        return nounou;
    }
}
