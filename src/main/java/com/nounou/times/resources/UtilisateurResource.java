package com.nounou.times.resources;

import com.nounou.times.dto.ErrorResponse;
import com.nounou.times.dto.LoginRequest;
import com.nounou.times.model.Utilisateur;
import com.nounou.times.services.UtilisateurService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

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
        return utilisateurService.findById(id)
                .map(u -> Response.ok(u).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response create(Utilisateur utilisateur) {
        utilisateurService.save(utilisateur);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Utilisateur utilisateur) {
        if (utilisateurService.findById(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        utilisateurService.update(utilisateur);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        if (utilisateurService.findById(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        utilisateurService.delete(id);
        return Response.noContent().build();
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        return utilisateurService.login(request.getEmail(), request.getPassword())
                .map(u -> {
                    String token = utilisateurService.generateToken(u);
                    return Response.ok(new LoginResponse(token, u)).build();
                })
                .orElse(Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("Email ou mot de passe incorrect")).build());
    }

    @POST
    @Path("/logout")
    public Response logout(@HeaderParam("Authorization") String token) {
        if (token == null || token.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Token manquant")).build();
        }
        if (utilisateurService.logout(token)) {
            return Response.ok().build();
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorResponse("Token invalide")).build();
    }

    public static class LoginResponse {
        private String token;
        private Utilisateur utilisateur;

        public LoginResponse(String token, Utilisateur utilisateur) {
            this.token = token;
            this.utilisateur = utilisateur;
        }

        public String getToken() { return token; }
        public Utilisateur getUtilisateur() { return utilisateur; }
    }
}
