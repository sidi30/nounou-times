package com.nounou.times.resources;

import com.nounou.times.model.Garde;
import com.nounou.times.services.GardeService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Path("/api/gardes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GardeResource {
    @Inject
    GardeService gardeService;

    @GET
    public Response getGardes(
            @HeaderParam("user-id") Long userId,
            @HeaderParam("user-role") String role,
            @QueryParam("debut") String debut,
            @QueryParam("fin") String fin) {
        try {
            List<Garde> gardes = gardeService.getGardes(userId, debut, fin);
            return Response.ok(gardes).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/jour")
    public Response getGardeJour(
            @HeaderParam("user-id") Long userId,
            @HeaderParam("user-role") String role) {
        try {
            Garde garde = gardeService.getGardeJour(userId);
            return garde != null ? 
                Response.ok(garde).build() : 
                Response.status(Response.Status.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/planifier")
    public Response planifier(
            @HeaderParam("user-id") Long userId,
            @HeaderParam("user-role") String role,
            PlanificationRequest request) {
        try {
            Garde garde = gardeService.planifier(
                userId,
                request.getEnfantId(),
                request.getDebut(),
                request.getFin(),
                request.isRepasInclus()
            );
            return Response.status(Response.Status.CREATED)
                    .entity(garde)
                    .build();
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
            @PathParam("id") Long gardeId) {
        try {
            gardeService.annuler(userId, gardeId);
            return Response.ok().build();
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
            @PathParam("id") Long gardeId,
            ModificationRequest request) {
        try {
            Garde garde = gardeService.modifier(
                userId,
                gardeId,
                request.getDebut(),
                request.getFin(),
                request.isRepasInclus()
            );
            return Response.ok(garde).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/{id}/depot")
    public Response validerDepot(
            @HeaderParam("user-id") Long userId,
            @HeaderParam("user-role") String role,
            @PathParam("id") Long gardeId) {
        try {
            gardeService.validerDepot(userId, gardeId);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/{id}/reprise")
    public Response validerReprise(
            @HeaderParam("user-id") Long userId,
            @HeaderParam("user-role") String role,
            @PathParam("id") Long gardeId) {
        try {
            gardeService.validerReprise(userId, gardeId);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/{id}/terminer")
    public Response terminer(
            @HeaderParam("user-id") Long userId,
            @HeaderParam("user-role") String role,
            @PathParam("id") Long gardeId,
            TerminaisonRequest request) {
        try {
            gardeService.terminer(userId, gardeId, request.getCommentaire());
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
}

@Getter
class PlanificationRequest {
    private Long enfantId;
    private LocalDateTime debut;
    private LocalDateTime fin;
    private boolean repasInclus;

    public void setEnfantId(Long enfantId) {
        this.enfantId = enfantId;
    }

    public void setDebut(LocalDateTime debut) {
        this.debut = debut;
    }

    public void setFin(LocalDateTime fin) {
        this.fin = fin;
    }

    public void setRepasInclus(boolean repasInclus) {
        this.repasInclus = repasInclus;
    }
}

class ModificationRequest {
    private LocalDateTime debut;
    private LocalDateTime fin;
    private boolean repasInclus;

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

    public boolean isRepasInclus() {
        return repasInclus;
    }

    public void setRepasInclus(boolean repasInclus) {
        this.repasInclus = repasInclus;
    }
}

class TerminaisonRequest {
    private String commentaire;

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}
