package com.nounou.times.resources;

import com.nounou.times.dto.AuthRequest;
import com.nounou.times.dto.AuthResponse;
import com.nounou.times.dto.RegisterRequest;
import com.nounou.times.model.Utilisateur;
import com.nounou.times.security.TokenService;
import com.nounou.times.services.UtilisateurService;
import jakarta.inject.Inject;
import jakarta.enterprise.inject.Instance;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;
import java.net.URI;
import java.time.Duration;
import org.mindrot.jbcrypt.BCrypt;

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
    Instance<JsonWebToken> idToken;

    private final String frontendUrl = "http://localhost:3000";

    @POST
    @Path("/register")
    public Response register(@Valid RegisterRequest request) {
        if (utilisateurService.findByEmail(request.getEmail()) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Email déjà utilisé")
                    .build();
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(hashPassword(request.getMotDePasse())); // Hash du mot de passe
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setCivilite(request.getCivilite());
        utilisateur.setTypeUtilisateur(request.getTypeUtilisateur());
        utilisateur.setAuthProvider("LOCAL");

        utilisateurService.save(utilisateur);
        return createAuthResponse(utilisateur);
    }

    @POST
    @Path("/login")
    public Response login(@Valid AuthRequest request) {
        Utilisateur utilisateur = utilisateurService.findByEmail(request.getEmail());
        boolean b = verifyPassword(request.getMotDePasse(), utilisateur.getMotDePasse());
        if (utilisateur == null || b) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Email ou mot de passe incorrect")
                    .build();
        }

        return createAuthResponse(utilisateur);
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    @GET
    @Path("/google/login")
    @Produces(MediaType.TEXT_HTML)
    public Response loginWithGoogle() {
        return Response.status(302)
                .location(URI.create("/q/oidc/login"))
                .cookie(new NewCookie.Builder("auth_flow")
                        .value("login")
                        .path("/")
                        .maxAge(3600)
                        .secure(false)
                        .httpOnly(true)
                        .sameSite(NewCookie.SameSite.LAX)
                        .build())
                .build();
    }

    @GET
    @Path("/google/register")
    @Produces(MediaType.TEXT_HTML)
    public Response registerWithGoogle(@QueryParam("type") String typeUtilisateur) {

        if(typeUtilisateur == null) {
            typeUtilisateur = "PARENT";
        }

        if (!typeUtilisateur.equals("PARENT") && !typeUtilisateur.equals("NOUNOU")) {
            return Response.status(400)
                    .entity("Le type d'utilisateur doit être PARENT ou NOUNOU")
                    .build();
        }
        return Response.status(302)
                .location(URI.create("/q/oidc/login"))
                .cookie(new NewCookie.Builder("auth_flow")
                        .value("register")
                        .path("/")
                        .maxAge(3600)
                        .secure(false)
                        .httpOnly(true)
                        .sameSite(NewCookie.SameSite.LAX)
                        .build())
                .cookie(new NewCookie.Builder("user_type")
                        .value(typeUtilisateur)
                        .path("/")
                        .maxAge(3600)
                        .secure(false)
                        .httpOnly(true)
                        .sameSite(NewCookie.SameSite.LAX)
                        .build())
                .build();
    }

    @GET
    @Path("/callback-google")
    public Response handleGoogleCallback(@Context SecurityContext securityContext,
                                       @CookieParam("auth_flow") String authFlow,
                                       @CookieParam("user_type") String userType) {
        try {
            if (!idToken.isResolvable()) {
                return redirectToFailure("Token Google non disponible");
            }

            JsonWebToken token = idToken.get();
            String email = token.getClaim("email");
            String name = token.getClaim("name");

            if (email == null) {
                return redirectToFailure("Email non fourni par Google");
            }

            Utilisateur utilisateur = utilisateurService.findByEmail(email);
            boolean isRegistration = "register".equals(authFlow);

            if (utilisateur != null && isRegistration) {
                return redirectToFailure("Cet email est déjà utilisé");
            }
            if (utilisateur == null && !isRegistration) {
                return redirectToFailure("Compte non trouvé. Veuillez vous inscrire");
            }

            if (utilisateur == null) {
                if (userType == null) {
                    return redirectToFailure("Type d'utilisateur non spécifié");
                }

                String[] nameParts = name != null ? name.split(" ") : new String[]{"", ""};
                utilisateur = new Utilisateur();
                utilisateur.setEmail(email);
                utilisateur.setNom(nameParts.length > 1 ? nameParts[1] : name);
                utilisateur.setPrenom(nameParts[0]);
                utilisateur.setTypeUtilisateur(userType);
                utilisateur.setAuthProvider("GOOGLE");
                utilisateur.setMotDePasse("");
                utilisateur.setCivilite("Non spécifié");

                try {
                    utilisateurService.save(utilisateur);
                } catch (Exception e) {
                    log.error("Erreur lors de la création du compte: " + e.getMessage());
                    return redirectToFailure("Erreur lors de la création du compte: " + e.getMessage());
                }
            }

            TokenService.TokenPair tokens;
            try {
                tokens = tokenService.generateTokens(utilisateur);
            } catch (Exception e) {
                return redirectToFailure("Erreur lors de la génération des tokens: " + e.getMessage());
            }

            String successUrl = frontendUrl + "/auth/success";
            return Response.status(302)
                    .location(URI.create(successUrl +
                            "?access_token=" + tokens.accessToken() +
                            "&refresh_token=" + tokens.refreshToken() +
                            (isRegistration ? "&new_account=true" : "")))
                    .cookie(new NewCookie.Builder("auth_flow")
                            .value("")
                            .path("/")
                            .maxAge(0)
                            .secure(false)
                            .httpOnly(true)
                            .sameSite(NewCookie.SameSite.LAX)
                            .build())
                    .cookie(new NewCookie.Builder("user_type")
                            .value("")
                            .path("/")
                            .maxAge(0)
                            .secure(false)
                            .httpOnly(true)
                            .sameSite(NewCookie.SameSite.LAX)
                            .build())
                    .build();

        } catch (Exception e) {
            return redirectToFailure("Erreur inattendue: " + e.getMessage());
        }
    }

    @GET
    @Path("/logout")
    public Response logout() {
        return Response.status(302)
                .location(URI.create("/q/oidc/logout"))
                .build();
    }

    @POST
    @Path("/refresh")
    public Response refresh(@HeaderParam("Refresh-Token") String refreshToken) {
        if (!tokenService.isRefreshTokenValid(refreshToken)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Refresh token invalide")
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
        return Response.ok(new AuthResponse(
            tokens.accessToken(),
            tokens.refreshToken(),
            Duration.ofMinutes(15).toSeconds(),
            createUserInfo(utilisateur)
        )).build();
    }

    private Response createAuthResponse(Utilisateur utilisateur) {
        TokenService.TokenPair tokens = tokenService.generateTokens(utilisateur);
        return Response.ok(new AuthResponse(
            tokens.accessToken(),
            tokens.refreshToken(),
            Duration.ofMinutes(15).toSeconds(),
            createUserInfo(utilisateur)
        )).build();
    }

    private Response redirectToFailure(String error) {
        String failureUrl = frontendUrl + "/auth/failure";
        return Response.status(302)
                .location(URI.create(failureUrl + "?error=" + error))
                .build();
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
