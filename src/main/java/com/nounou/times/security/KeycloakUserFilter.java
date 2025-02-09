package com.nounou.times.security;

import com.nounou.times.model.Utilisateur;
import com.nounou.times.services.UtilisateurService;
import io.quarkus.oidc.IdToken;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Provider
@ApplicationScoped
public class KeycloakUserFilter implements ContainerRequestFilter {

    @Inject
    @IdToken
    JsonWebToken idToken;

    @Inject
    UtilisateurService utilisateurService;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (idToken != null && idToken.getSubject() != null) {
            String email = idToken.getClaim("email");
            if (email != null) {
                Utilisateur utilisateur = utilisateurService.findByEmail(email);
                if (utilisateur == null) {
                    // Créer un nouvel utilisateur à partir des informations Keycloak
                    utilisateur = new Utilisateur();
                    utilisateur.setEmail(email);
                    utilisateur.setNom(idToken.getClaim("family_name"));
                    utilisateur.setPrenom(idToken.getClaim("given_name"));
                    utilisateur.setCivilite("Non spécifié"); // À définir selon vos besoins
                    utilisateur.setTypeUtilisateur(getDefaultRole());
                    utilisateur.setMotDePasse("KEYCLOAK_USER"); // Mot de passe fictif car géré par Keycloak
                    utilisateurService.save(utilisateur);
                }
            }
        }
    }

    private String getDefaultRole() {
        return "PARENT"; // Vous pouvez ajuster selon vos besoins
    }
}
