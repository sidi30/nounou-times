package com.nounou.times.resources;

import com.nounou.times.model.Parent;
import com.nounou.times.services.ParentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.Data;

import java.util.List;

@Path("/parents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ParentResource {
    
    @Inject
    ParentService parentService;

    @GET
    public List<Parent> getAll() {
        return parentService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Parent parent = parentService.findById(id);
        if (parent == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(parent).build();
    }

    @POST
    public Response create(Parent parent) {
        // Vérifier si l'email existe déjà
        if (parentService.findByEmail(parent.getEmail()) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Un compte existe déjà avec cet email")
                    .build();
        }
        
        parentService.save(parent);
        return Response.status(Response.Status.CREATED).entity(parent).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Parent parent) {
        Parent existing = parentService.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Vérifier si le nouvel email n'est pas déjà utilisé par un autre parent
        Parent parentWithEmail = parentService.findByEmail(parent.getEmail());
        if (parentWithEmail != null && !parentWithEmail.getId().equals(id)) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Cet email est déjà utilisé par un autre compte")
                    .build();
        }

        parent.setId(id);
        parentService.update(parent);
        return Response.ok(parent).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        Parent parent = parentService.findById(id);
        if (parent == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        parentService.delete(id);
        return Response.noContent().build();
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest loginRequest) {
        Parent parent = parentService.login(loginRequest.getEmail(), loginRequest.getMotDePasse());
        if (parent == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Email ou mot de passe incorrect")
                    .build();
        }

        String token = parentService.generateToken(parent);
        return Response.ok(new LoginResponse(token, parent)).build();
    }

    @POST
    @Path("/logout")
    public Response logout(@HeaderParam("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Token manquant")
                    .build();
        }

        if (parentService.logout(token)) {
            return Response.ok("Déconnexion réussie").build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Token invalide")
                    .build();
        }
    }

    @Data
    public static class LoginRequest {
        private String email;
        private String motDePasse;
    }

    @Data
    public static class LoginResponse {
        private String token;
        private Parent parent;

        public LoginResponse(String token, Parent parent) {
            this.token = token;
            this.parent = parent;
        }
    }
}
