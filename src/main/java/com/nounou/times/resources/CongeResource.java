package com.nounou.times.resources;

import com.nounou.times.model.Conge;
import com.nounou.times.services.CongeService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

@Path("/api/conges")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CongeResource {
    @Inject
    CongeService congeService;

    @GET
    public Response getConges(
            @HeaderParam("user-id") Long userId,
            @HeaderParam("user-role") String role) {
        try {
            List<Conge> conges = congeService.getConges(userId);
            return Response.ok(conges).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    public Response demander(
            @HeaderParam("user-id") Long userId,
            @HeaderParam("user-role") String role,
            CongeRequest request) {
        try {
            Conge conge = congeService.demander(
                userId,
                request.getDebut(),
                request.getFin(),
                request.getType(),
                request.getMotif()
            );
            return Response.status(Response.Status.CREATED)
                    .entity(conge)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response modifier(
            @HeaderParam("user-id") Long userId,
            @HeaderParam("user-role") String role,
            @PathParam("id") Long congeId,
            ModificationRequest request) {
        try {
            Conge conge = congeService.modifier(
                userId,
                congeId,
                request.getDebut(),
                request.getFin(),
                request.getMotif()
            );
            return Response.ok(conge).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/{id}/annuler")
    public Response annuler(
            @HeaderParam("user-id") Long userId,
            @HeaderParam("user-role") String role,
            @PathParam("id") Long congeId) {
        try {
            congeService.annuler(userId, congeId);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/{id}/repondre")
    public Response repondre(
            @HeaderParam("user-id") Long userId,
            @HeaderParam("user-role") String role,
            @PathParam("id") Long congeId,
            ReponseRequest request) {
        try {
            congeService.repondre(
                userId,
                congeId,
                request.getDecision(),
                request.getCommentaire()
            );
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
}

class CongeRequest {
    private LocalDateTime debut;
    private LocalDateTime fin;
    private String type;
    private String motif;

    public LocalDateTime getDebut() {
        return debut;
    }

    public void setDebut(LocalDateTime debut) {
        this.debut = debut;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public void setFin(LocalDateTime fin) {
        this.fin = fin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }
}


class ReponseRequest {
    private String decision;
    private String commentaire;

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}
