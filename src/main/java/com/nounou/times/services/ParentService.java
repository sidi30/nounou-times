package com.nounou.times.services;

import com.nounou.times.model.Parent;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class ParentService {
    
    @Inject
    PanacheRepository<Parent> parentRepository;

    public Parent findById(Long id) {
        return parentRepository.findById(id);
    }

    public List<Parent> findAll() {
        return parentRepository.listAll();
    }

    public Parent findByEmail(String email) {
        return parentRepository.find("email", email).firstResult();
    }

    @Transactional
    public void save(Parent parent) {
        parentRepository.persist(parent);
    }

    @Transactional
    public void update(Parent parent) {
        parentRepository.persist(parent);
    }

    @Transactional
    public void delete(Long id) {
        parentRepository.deleteById(id);
    }

    public Parent login(String email, String motDePasse) {
        return parentRepository.find("email = ?1 and motDePasse = ?2", email, motDePasse).firstResult();
    }

    // Gestion des sessions
    private Set<String> activeTokens = new HashSet<>();

    public String generateToken(Parent parent) {
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
