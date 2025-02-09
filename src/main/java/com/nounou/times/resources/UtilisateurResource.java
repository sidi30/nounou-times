package com.nounou.times.resources;

import com.nounou.times.model.Utilisateur;
import com.nounou.times.services.UtilisateurService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

// Resource: UtilisateurResource
@Path("/utilisateurs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UtilisateurResource {
    @Inject
    UtilisateurService utilisateurService;

    @GET
    public List<Utilisateur> getAll() {
        return utilisateurService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Utilisateur utilisateur = utilisateurService.findById(id);
        if (utilisateur == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(utilisateur).build();
    }

    @POST
    public Response create(Utilisateur utilisateur) {
        utilisateurService.save(utilisateur);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Utilisateur utilisateur) {
        Utilisateur existing = utilisateurService.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        utilisateurService.update(utilisateur);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        utilisateurService.delete(id);
        return Response.noContent().build();
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest loginRequest) {
        Utilisateur utilisateur = utilisateurService.login(loginRequest.getEmail(), loginRequest.getMotDePasse());
        if (utilisateur == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Email ou mot de passe incorrect")
                    .build();
        }

        String token = utilisateurService.generateToken(utilisateur);
        return Response.ok(new LoginResponse(token, utilisateur)).build();
    }

    @POST
    @Path("/logout")
    public Response logout(@HeaderParam("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Token manquant")
                    .build();
        }

        if (utilisateurService.logout(token)) {
            return Response.ok("Déconnexion réussie").build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Token invalide")
                    .build();
        }
    }

    // Classes DTO pour le login
    public static class LoginRequest {
        private String email;
        private String motDePasse;

        // Getters et setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMotDePasse() {
            return motDePasse;
        }

        public void setMotDePasse(String motDePasse) {
            this.motDePasse = motDePasse;
        }
    }

    public static class LoginResponse {
        private String token;
        private Utilisateur utilisateur;

        public LoginResponse(String token, Utilisateur utilisateur) {
            this.token = token;
            this.utilisateur = utilisateur;
        }

        // Getters et setters
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Utilisateur getUtilisateur() {
            return utilisateur;
        }

        public void setUtilisateur(Utilisateur utilisateur) {
            this.utilisateur = utilisateur;
        }
    }
}