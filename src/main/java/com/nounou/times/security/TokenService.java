package com.nounou.times.security;

import com.nounou.times.model.Utilisateur;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.jwt.Claims;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class TokenService {

    public String generateToken(Utilisateur utilisateur) {
        Set<String> roles = new HashSet<>();
        roles.add(utilisateur.getTypeUtilisateur());

        return Jwt.issuer("nounou-times")
                .subject(utilisateur.getEmail())
                .groups(roles)
                .claim(Claims.full_name, utilisateur.getNom() + " " + utilisateur.getPrenom())
                .claim("id", utilisateur.id)
                .expiresIn(Duration.ofHours(24))
                .sign();
    }
}
