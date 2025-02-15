package com.nounou.times.security;

import com.nounou.times.model.Utilisateur;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.Claims;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class TokenService {
    @Inject
    KeyGenerator keyGenerator;

    private final ConcurrentHashMap<String, RefreshTokenInfo> refreshTokens = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> invalidatedTokens = new ConcurrentHashMap<>();

    private static final Duration ACCESS_TOKEN_DURATION = Duration.ofMinutes(15);
    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(30);

    public record RefreshTokenInfo(String email, Instant expirationTime) {}

    public boolean isRefreshTokenValid(String refreshToken) {
        RefreshTokenInfo info = refreshTokens.get(refreshToken);
        if (info == null) {
            return false;
        }
        
        if (Instant.now().isAfter(info.expirationTime())) {
            refreshTokens.remove(refreshToken);
            return false;
        }
        
        return true;
    }

    public String getUserEmailFromRefreshToken(String refreshToken) {
        RefreshTokenInfo info = refreshTokens.get(refreshToken);
        return info != null ? info.email() : null;
    }

    public TokenPair generateTokens(Utilisateur utilisateur) {
        String accessToken = generateAccessToken(utilisateur);
        String refreshToken = generateRefreshToken(utilisateur);
        return new TokenPair(accessToken, refreshToken);
    }

    private String generateRefreshToken(Utilisateur utilisateur) {
        String refreshToken = UUID.randomUUID().toString();
        refreshTokens.put(refreshToken, new RefreshTokenInfo(
            utilisateur.getEmail(),
            Instant.now().plus(REFRESH_TOKEN_DURATION)
        ));
        return refreshToken;
    }

    private String generateAccessToken(Utilisateur utilisateur) {
        Set<String> groups = new HashSet<>();
        groups.add(utilisateur.getTypeUtilisateur());

        return Jwt.claims()
                .issuer("nounou-times")
                .subject(utilisateur.getEmail())
                .groups(groups)
                .claim(Claims.full_name.name(), utilisateur.getNom() + " " + utilisateur.getPrenom())
                .claim(Claims.email.name(), utilisateur.getEmail())
                .claim("user_type", utilisateur.getTypeUtilisateur())
                .claim("user_id", utilisateur.getId())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(ACCESS_TOKEN_DURATION))
                .jws()
                .keyId("nounou-times-key")
                .sign(keyGenerator.getPrivateKey());
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

    public record TokenPair(String accessToken, String refreshToken) {}
}
