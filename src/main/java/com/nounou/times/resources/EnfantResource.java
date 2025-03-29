package com.nounou.times.resources;

import com.nounou.times.model.Enfant;
import com.nounou.times.services.EnfantService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

@Path("/api/enfants")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EnfantResource {
    @Inject
    EnfantService enfantService;

    @GET
    public Response getEnfants(
            @HeaderParam("user-id") Long userId,
            @HeaderParam("user-role") String role) {
        try {
            List<Enfant> enfants = enfantService.getEnfantsNounou(userId);
            return Response.ok(enfants).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/inviter")
    public Response envoyerInvitation(
            @HeaderParam("user-id") Long userId,
            @HeaderParam("user-role") String role,
            InvitationRequest invitation) {
        try {
            enfantService.envoyerInvitation(userId, invitation);
            return Response.status(Response.Status.CREATED).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/invitations/{token}/accepter")
    public Response accepterInvitation(
            @PathParam("token") String token) {
        try {
            enfantService.accepterInvitation(token);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/invitations/{token}/refuser")
    public Response refuserInvitation(
            @PathParam("token") String token) {
        try {
            enfantService.refuserInvitation(token);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response mettreAJour(
            @HeaderParam("user-id") Long userId,
            @HeaderParam("user-role") String role,
            @PathParam("id") Long enfantId,
            ModificationRequest request) {
        try {
            enfantService.mettreAJour(userId, enfantId, 
                request.getNom(), request.getPrenom(), request.getDateNaissance());
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/{id}/terminer")
    public Response terminerGarde(
            @HeaderParam("user-id") Long userId,
            @HeaderParam("user-role") String role,
            @PathParam("id") Long enfantId) {
        try {
            enfantService.terminerGarde(userId, enfantId);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
}

class InvitationRequest {
    private String nomEnfant;
    private String prenomEnfant;
    private LocalDateTime dateNaissance;
    private String emailParent;

    public String getNomEnfant() {
        return nomEnfant;
    }

    public void setNomEnfant(String nomEnfant) {
        this.nomEnfant = nomEnfant;
    }

    public String getPrenomEnfant() {
        return prenomEnfant;
    }

    public void setPrenomEnfant(String prenomEnfant) {
        this.prenomEnfant = prenomEnfant;
    }

    public LocalDateTime getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDateTime dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getEmailParent() {
        return emailParent;
    }

    public void setEmailParent(String emailParent) {
        this.emailParent = emailParent;
    }
}

class ModificationRequest {
    private String nom;
    private String prenom;
    private LocalDateTime dateNaissance;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDateTime getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDateTime dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
}