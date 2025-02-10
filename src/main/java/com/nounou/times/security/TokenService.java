package com.nounou.times.security;

import com.nounou.times.model.Utilisateur;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import org.eclipse.microprofile.jwt.Claims;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class TokenService {
    private final ConcurrentHashMap<String, String> refreshTokens = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> invalidatedTokens = new ConcurrentHashMap<>();

    private static final Duration ACCESS_TOKEN_DURATION = Duration.ofMinutes(15);
    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(30);

    public TokenPair generateTokens(Utilisateur utilisateur) {
        String accessToken = generateAccessToken(utilisateur);
        String refreshToken = generateRefreshToken(utilisateur);
        return new TokenPair(accessToken, refreshToken);
    }

    public String generateAccessToken(Utilisateur utilisateur) {
        Set<String> roles = new HashSet<>();
        roles.add(utilisateur.getTypeUtilisateur());

        return Jwt.issuer("nounou-times")
                .subject(utilisateur.getEmail())
                .groups(roles)
                .claim(Claims.full_name, utilisateur.getNom() + " " + utilisateur.getPrenom())
                .claim("id", utilisateur.id)
                .expiresIn(ACCESS_TOKEN_DURATION)
                .sign();
    }

    private String generateRefreshToken(Utilisateur utilisateur) {
        String refreshToken = UUID.randomUUID().toString();
        refreshTokens.put(refreshToken, utilisateur.getEmail());
        return refreshToken;
    }

    public boolean isRefreshTokenValid(String refreshToken) {
        return refreshTokens.containsKey(refreshToken);
    }

    public String getUserEmailFromRefreshToken(String refreshToken) {
        return refreshTokens.get(refreshToken);
    }

    public void invalidateRefreshToken(String refreshToken) {
        refreshTokens.remove(refreshToken);
    }

    public void invalidateAccessToken(String token) {
        invalidatedTokens.put(token, System.currentTimeMillis() + ACCESS_TOKEN_DURATION.toMillis());
    }

    public boolean isAccessTokenInvalidated(String token) {
        Long expirationTime = invalidatedTokens.get(token);
        if (expirationTime == null) {
            return false;
        }
        
        if (System.currentTimeMillis() > expirationTime) {
            invalidatedTokens.remove(token);
            return false;
        }
        
        return true;
    }

    public boolean invalidateToken(String token) {
        if (refreshTokens.containsKey(token)) {
            refreshTokens.remove(token);
            return true;
        }

        if (invalidatedTokens.containsKey(token)) {
            invalidatedTokens.remove(token);
            return true;
        }

        return false;
    }

        public record TokenPair(String accessToken, String refreshToken) {

    }
}
