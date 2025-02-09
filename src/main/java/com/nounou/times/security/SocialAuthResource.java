package com.nounou.times.security;

import com.nounou.times.model.Utilisateur;
import com.nounou.times.services.UtilisateurService;
import io.quarkus.oidc.IdToken;
import io.quarkus.oidc.OidcSession;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SocialAuthResource {

    @Inject
    UtilisateurService utilisateurService;

    @Inject
    @IdToken
    JsonWebToken idToken;

    @Inject
    OidcSession oidcSession;

    @ConfigProperty(name = "application.frontend.url")
    String frontendUrl;

    @ConfigProperty(name = "application.auth.success-url")
    String successUrl;

    @ConfigProperty(name = "application.auth.failure-url")
    String failureUrl;

    @GET
    @Path("/google")
    public Response loginGoogle() {
        return Response.status(302)
                .header("Location", "/oauth2/authorization/google")
                .build();
    }

    @GET
    @Path("/callback-google")
    public Response handleGoogleCallback() {
        if (idToken == null || idToken.getSubject() == null) {
            return redirectToFrontend(failureUrl);
        }

        String email = idToken.getClaim("email");
        String name = idToken.getClaim("name");
        String picture = idToken.getClaim("picture");
        String locale = idToken.getClaim("locale");

        try {
            Utilisateur utilisateur = utilisateurService.findByEmail(email);
            if (utilisateur == null) {
                // Créer un nouvel utilisateur avec les informations de Google
                utilisateur = new Utilisateur();
                utilisateur.setEmail(email);
                
                // Diviser le nom complet en prénom et nom
                String[] nameParts = name.split(" ", 2);
                utilisateur.setPrenom(nameParts[0]);
                utilisateur.setNom(nameParts.length > 1 ? nameParts[1] : "");
                
                utilisateur.setCivilite("Non spécifié");
                utilisateur.setTypeUtilisateur("PARENT"); // Rôle par défaut
                utilisateur.setMotDePasse("GOOGLE_USER");
                utilisateurService.save(utilisateur);
            }

            // Générer un token JWT pour l'utilisateur
            String token = utilisateurService.generateSocialToken(utilisateur);
            return redirectToFrontend(successUrl + "?token=" + token);

        } catch (Exception e) {
            return redirectToFrontend(failureUrl);
        }
    }

    private Response redirectToFrontend(String path) {
        return Response.status(302)
                .header("Location", frontendUrl + path)
                .build();
    }

    @GET
    @Path("/user")
    public Response getCurrentUser() {
        if (idToken == null || idToken.getSubject() == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return Response.ok(new UserInfo(
            idToken.getClaim("name"),
            idToken.getClaim("email"),
            idToken.getClaim("picture")
        )).build();
    }

    public static class UserInfo {
        public String name;
        public String email;
        public String picture;

        public UserInfo(String name, String email, String picture) {
            this.name = name;
            this.email = email;
            this.picture = picture;
        }
    }
}
