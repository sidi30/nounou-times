package com.nounou.times.resources;

import com.nounou.times.dto.AuthResponse;
import com.nounou.times.model.Utilisateur;
import com.nounou.times.security.TokenService;
import com.nounou.times.services.UtilisateurService;
import io.quarkus.oidc.IdToken;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import io.quarkus.oidc.OidcSession;

@Slf4j
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    UtilisateurService utilisateurService;

    @Inject
    TokenService tokenService;

    @Inject
    @IdToken
    JsonWebToken idToken;

    @Inject
    OidcSession oidcSession;

    @ConfigProperty(name = "quarkus.oidc.client-id")
    String clientId;

    @ConfigProperty(name = "quarkus.oidc.authentication.redirect-path")
    String redirectPath;

    private final String frontendUrl = "http://localhost:3000";

    @GET
    @Path("/google/login")
    public Response loginWithGoogle() {
        try {
            log.info("Démarrage de l'authentification Google");

            // Construction de l'URL de redirection vers Google
            String baseUrl = "http://localhost:8080";  // URL de base de votre backend
            String state = java.util.UUID.randomUUID().toString();

            // Construire l'URL de redirection OAuth2
            String googleAuthUrl = String.format("%s/q/oidc/login?redirect_uri=%s/auth/callback&state=%s",
                    baseUrl,
                    URLEncoder.encode(baseUrl, StandardCharsets.UTF_8),
                    URLEncoder.encode(state, StandardCharsets.UTF_8));

            log.info("Redirection vers l'URL de connexion Google: {}", googleAuthUrl);

            return Response.status(302)
                    .location(URI.create(googleAuthUrl))
                    .cookie(new NewCookie.Builder("oauth_state")
                            .value(state)
                            .path("/")
                            .maxAge(600) // 10 minutes
                            .httpOnly(true)
                            .build())
                    .build();

        } catch (Exception e) {
            log.error("Erreur lors de la redirection vers Google: {}", e.getMessage(), e);
            return Response.serverError()
                    .entity("Erreur lors de la redirection vers Google")
                    .build();
        }
    }

    @GET
    @Path("/callback")
    public Response handleGoogleCallback(@Context SecurityContext securityContext) {
        try {
            log.info("Réception du callback Google");

            if (idToken == null) {
                log.error("Token Google non disponible");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Token Google non disponible")
                        .build();
            }

            String email = idToken.getClaim("email");
            String name = idToken.getClaim("name");
            String picture = idToken.getClaim("picture");

            log.info("Traitement de l'authentification pour l'email: {}", email);

            if (email == null) {
                log.error("Email non fourni par Google");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Email non fourni par Google")
                        .build();
            }

            email = email.toLowerCase().trim();
            Utilisateur utilisateur = utilisateurService.findByEmail(email);

            if (utilisateur == null) {
                log.info("Création d'un nouveau compte pour: {}", email);
                String[] nameParts = name != null ? name.split(" ") : new String[]{"", ""};
                utilisateur = new Utilisateur();
                utilisateur.setEmail(email);
                utilisateur.setNom(nameParts.length > 1 ? nameParts[1] : "");
                utilisateur.setPrenom(nameParts[0]);
                utilisateur.setTypeUtilisateur("PARENT");
                utilisateur.setAuthProvider("GOOGLE");
                utilisateur.setMotDePasse("");
                utilisateur.setCivilite("Non spécifié");

                try {
                    utilisateurService.save(utilisateur);
                    log.info("Nouveau compte créé avec succès pour: {}", email);
                } catch (Exception e) {
                    log.error("Erreur lors de la création du compte: {}", e.getMessage(), e);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Erreur lors de la création du compte")
                            .build();
                }
            }

            TokenService.TokenPair tokens = tokenService.generateTokens(utilisateur);
            log.info("Tokens générés avec succès pour: {}", email);

            String successUrl = frontendUrl + "/auth/success";
            return Response.status(302)
                    .location(URI.create(successUrl))
                    .header("Authorization", "Bearer " + tokens.accessToken())
                    .header("Refresh-Token", tokens.refreshToken())
                    .build();

        } catch (Exception e) {
            log.error("Erreur lors de l'authentification Google: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de l'authentification")
                    .build();
        }
    }

    @POST
    @Path("/refresh")
    public Response refresh(@HeaderParam("Refresh-Token") String refreshToken) {
        try {
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Refresh token manquant")
                        .build();
            }

            if (!tokenService.isRefreshTokenValid(refreshToken)) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Refresh token invalide ou expiré")
                        .build();
            }

            String email = tokenService.getUserEmailFromRefreshToken(refreshToken);
            Utilisateur utilisateur = utilisateurService.findByEmail(email);

            if (utilisateur == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Utilisateur non trouvé")
                        .build();
            }

            TokenService.TokenPair tokens = tokenService.generateTokens(utilisateur);
            return Response.ok()
                    .header("Authorization", "Bearer " + tokens.accessToken())
                    .header("Refresh-Token", tokens.refreshToken())
                    .entity(new AuthResponse(
                        tokens.accessToken(),
                        tokens.refreshToken(),
                        Duration.ofMinutes(15).toSeconds(),
                        createUserInfo(utilisateur)
                    )).build();
        } catch (Exception e) {
            log.error("Erreur lors du rafraîchissement du token: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Une erreur est survenue lors du rafraîchissement du token")
                    .build();
        }
    }

    @GET
    @Path("/logout")
    @Authenticated
    public Response logout(@HeaderParam("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                tokenService.invalidateAccessToken(token);
            }

            if (oidcSession != null) {
                oidcSession.logout();
            }

            return Response.status(302)
                    .location(URI.create(frontendUrl))
                    .build();
        } catch (Exception e) {
            log.error("Erreur lors de la déconnexion: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Une erreur est survenue lors de la déconnexion")
                    .build();
        }
    }

    private Response redirectToFailure(String error) {
        String failureUrl = frontendUrl + "/auth/failure";
        try {
            String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8);
            return Response.status(302)
                    .location(URI.create(failureUrl + "?error=" + encodedError))
                    .build();
        } catch (Exception e) {
            log.error("Erreur lors de l'encodage de l'URL: {}", e.getMessage());
            return Response.status(302)
                    .location(URI.create(failureUrl))
                    .build();
        }
    }

    private AuthResponse.UserInfo createUserInfo(Utilisateur utilisateur) {
        return new AuthResponse.UserInfo(
            utilisateur.getEmail(),
            utilisateur.getNom(),
            utilisateur.getPrenom(),
            utilisateur.getCivilite(),
            utilisateur.getTypeUtilisateur()
        );
    }
}
