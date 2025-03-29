package com.nounou.times.resources;

import com.nounou.times.model.Reclamation;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

@Path("/api/reclamations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReclamationResource {
    @Inject
    ReclamationService reclamationService;

    @GET
    public Response getReclamations(
            @HeaderParam("user-id") Long userId,
            @HeaderParam("user-role") String role) {
        try {
            List<Reclamation> reclamations = reclamationService.getReclamations(userId);
            return Response.ok(reclamations).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    public Response creer(
            @HeaderParam("user-id") Long userId,
            @HeaderParam("user-role") String role,
            ReclamationRequest request) {
        try {
            Reclamation reclamation = reclamationService.creer(
                userId,
                request.getGardeId(),
                request.getType(),
                request.getDescription(),
                request.getHeuresReclamees()
            );
            return Response.status(Response.Status.CREATED)
                    .entity(reclamation)
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
            @PathParam("id") Long reclamationId,
            ModificationRequest request) {
        try {
            Reclamation reclamation = reclamationService.modifier(
                userId,
                reclamationId,
                request.getDescription(),
                request.getHeuresReclamees()
            );
            return Response.ok(reclamation).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/{id}/traiter")
    public Response traiter(
            @HeaderParam("user-id") Long userId,
            @HeaderParam("user-role") String role,
            @PathParam("id") Long reclamationId,
            TraitementRequest request) {
        try {
            reclamationService.traiter(
                userId,
                reclamationId,
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

class ReclamationRequest {
    private Long gardeId;
    private String type;
    private String description;
    private double heuresReclamees;

    public Long getGardeId() {
        return gardeId;
    }

    public void setGardeId(Long gardeId) {
        this.gardeId = gardeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getHeuresReclamees() {
        return heuresReclamees;
    }

    public void setHeuresReclamees(double heuresReclamees) {
        this.heuresReclamees = heuresReclamees;
    }
}

class ModificationRequest {
    private String description;
    private double heuresReclamees;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getHeuresReclamees() {
        return heuresReclamees;
    }

    public void setHeuresReclamees(double heuresReclamees) {
        this.heuresReclamees = heuresReclamees;
    }
}

class TraitementRequest {
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
