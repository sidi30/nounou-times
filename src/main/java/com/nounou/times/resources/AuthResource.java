package com.nounou.times.resources;

import com.nounou.times.dto.AuthRequest;
import com.nounou.times.dto.AuthResponse;
import com.nounou.times.dto.RegisterRequest;
import com.nounou.times.model.Utilisateur;
import com.nounou.times.security.TokenService;
import com.nounou.times.services.UtilisateurService;
import io.quarkus.oidc.IdToken;
import jakarta.inject.Inject;
import jakarta.enterprise.inject.Instance;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.Duration;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    UtilisateurService utilisateurService;

    @Inject
    TokenService tokenService;

    @Inject
    Instance<JsonWebToken> idToken;

    private final String successUrl = "http://localhost:3000/auth/success";
    private final String failureUrl = "http://localhost:3000/auth/failure";

    @POST
    @Path("/register")
    public Response register(@Valid RegisterRequest request) {
        // Vérifier si l'email existe déjà
        if (utilisateurService.findByEmail(request.getEmail()) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse("Email déjà utilisé"))
                    .build();
        }

        // Créer le nouvel utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(request.getMotDePasse());
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setCivilite(request.getCivilite());
        utilisateur.setTypeUtilisateur(request.getTypeUtilisateur());

        utilisateurService.save(utilisateur);

        // Générer les tokens
        TokenService.TokenPair tokens = tokenService.generateTokens(utilisateur);

        return Response.ok(new AuthResponse(
            tokens.accessToken(),
            tokens.refreshToken(),
            Duration.ofMinutes(15).toSeconds(),
            createUserInfo(utilisateur)
        )).build();
    }

    @POST
    @Path("/login")
    public Response login(@Valid AuthRequest request) {
        try {
            Utilisateur utilisateur = utilisateurService.authenticate(request.getEmail(), request.getMotDePasse());
            TokenService.TokenPair tokens = tokenService.generateTokens(utilisateur);

            return Response.ok(new AuthResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                Duration.ofMinutes(15).toSeconds(),
                createUserInfo(utilisateur)
            )).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("Email ou mot de passe incorrect"))
                    .build();
        }
    }

    @POST
    @Path("/refresh")
    public Response refresh(@HeaderParam("Refresh-Token") String refreshToken) {
        if (!tokenService.isRefreshTokenValid(refreshToken)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("Refresh token invalide"))
                    .build();
        }

        String email = tokenService.getUserEmailFromRefreshToken(refreshToken);
        Utilisateur utilisateur = utilisateurService.findByEmail(email);
        
        if (utilisateur == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("Utilisateur non trouvé"))
                    .build();
        }

        TokenService.TokenPair tokens = tokenService.generateTokens(utilisateur);

        return Response.ok(new AuthResponse(
            tokens.accessToken(),
            tokens.refreshToken(),
            Duration.ofMinutes(15).toSeconds(),
            createUserInfo(utilisateur)
        )).build();
    }

    @POST
    @Path("/logout")
    public Response logout(@HeaderParam("Authorization") String authorization,
                         @HeaderParam("Refresh-Token") String refreshToken) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            tokenService.invalidateToken(token);
        }

        if (refreshToken != null) {
            tokenService.invalidateToken(refreshToken);
        }

        return Response.ok().build();
    }

    @GET
    @Path("/login/google")
    public Response loginWithGoogle() {
        return Response.status(302)
                .header("Location", "/oauth2/authorization/google")
                .build();
    }

    @GET
    @Path("/callback-google")
    public Response handleGoogleCallback() {
        if (!idToken.isResolvable() || idToken.get() == null) {
            return redirectToFrontend(failureUrl + "?error=OIDC_not_configured");
        }

        try {
            JsonWebToken token = idToken.get();
            String email = token.getClaim("email");
            String name = token.getClaim("name");
            String picture = token.getClaim("picture");

            if (email == null) {
                return redirectToFrontend(failureUrl + "?error=Email_not_found");
            }

            Utilisateur utilisateur = utilisateurService.findByEmail(email);
            if (utilisateur == null) {
                utilisateur = new Utilisateur();
                utilisateur.setEmail(email);
                String[] nameParts = name != null ? name.split(" ") : new String[]{"", ""};
                utilisateur.setNom(nameParts.length > 1 ? nameParts[1] : "");
                utilisateur.setPrenom(nameParts[0]);
                utilisateur.setCivilite("Non spécifié");
                utilisateur.setTypeUtilisateur("PARENT");
                utilisateur.setMotDePasse("GOOGLE_USER");
                utilisateurService.save(utilisateur);
            }

            TokenService.TokenPair tokens = tokenService.generateTokens(utilisateur);

            return Response.ok(new AuthResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                Duration.ofMinutes(15).toSeconds(),
                createUserInfo(utilisateur)
            )).build();

        } catch (Exception e) {
            return redirectToFrontend(failureUrl + "?error=" + e.getMessage());
        }
    }

    private Response redirectToFrontend(String url) {
        return Response.status(302)
                .header("Location", url)
                .build();
    }

    private AuthResponse.UserInfo createUserInfo(Utilisateur utilisateur) {
        return new AuthResponse.UserInfo(
            null,
            utilisateur.getEmail(),
            utilisateur.getNom(),
            utilisateur.getPrenom(),
            utilisateur.getCivilite(),
            utilisateur.getTypeUtilisateur(),
            null
        );
    }

    private static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
