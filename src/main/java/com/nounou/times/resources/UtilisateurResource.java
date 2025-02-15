package com.nounou.times.resources;

import com.nounou.times.model.Utilisateur;
import com.nounou.times.security.TokenService;
import com.nounou.times.services.UtilisateurService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
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
    @RolesAllowed({"PARENT", "NOUNOU"})
    public List<Utilisateur> getAll() {
        return utilisateurService.findAll();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"PARENT", "NOUNOU"})
    public Response getById(@PathParam("id") Long id) {
        Utilisateur utilisateur = utilisateurService.findById(id);
        if (utilisateur == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(utilisateur).build();
    }

    @POST
    @PermitAll
    public Response create(Utilisateur utilisateur) {
        utilisateurService.save(utilisateur);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"PARENT", "NOUNOU"})
    public Response update(@PathParam("id") Long id, Utilisateur utilisateur) {
        utilisateur.setId(id);
        utilisateurService.update(utilisateur);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"PARENT", "NOUNOU"})
    public Response delete(@PathParam("id") Long id) {
        utilisateurService.delete(id);
        return Response.noContent().build();
    }

    @POST
    @Path("/logout")
    @RolesAllowed({"PARENT", "NOUNOU"})
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
}