package com.nounou.times.resources;

import com.nounou.times.model.*;
import com.nounou.times.services.ParentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/parents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ParentRessource {

    @Inject
    ParentService parentService;

    // 2.1. Inscription et connexion
    @POST
    @Path("/signup")
    public Response signup(Parent parent) {
        try {
            Parent newParent = parentService.signup(parent);
            return Response.status(Response.Status.CREATED).entity(newParent).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/signup/page-emploi")
    public Response signupWithPageEmploi(String pageEmploiId) {
        try {
            Parent parent = parentService.signupWithPageEmploi(pageEmploiId);
            return Response.status(Response.Status.CREATED).entity(parent).build();
        } catch (UnsupportedOperationException e) {
            return Response.status(Response.Status.NOT_IMPLEMENTED).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest loginRequest) {
        try {
            Parent parent = parentService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return Response.ok(parent).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }

    // 2.2. Gestion des enfants et nounous
    @GET
    @Path("/enfants")
    public Response getEnfants(@HeaderParam("parent-id") Long parentId) {
        List<Enfant> enfants = parentService.getEnfants(parentId);
        return Response.ok(enfants).build();
    }

    @GET
    @Path("/nounous")
    public Response getNounous(@HeaderParam("parent-id") Long parentId) {
        List<Nounou> nounous = parentService.getNounous(parentId);
        return Response.ok(nounous).build();
    }

    @PATCH
    @Path("/nounous/{id}/accept")
    public Response acceptNounou(@HeaderParam("parent-id") Long parentId, @PathParam("id") Long nounouId) {
        try {
            parentService.acceptNounou(parentId, nounouId);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // 2.3. Gestion des gardes et des imprévus
    @GET
    @Path("/garde/jour")
    public Response getGardeJour(@HeaderParam("parent-id") Long parentId) {
        Garde garde = parentService.getGardeJour(parentId);
        return garde != null ? Response.ok(garde).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @PATCH
    @Path("/garde/{id}/depot")
    public Response validerDepot(@HeaderParam("parent-id") Long parentId, @PathParam("id") Long gardeId) {
        try {
            parentService.validerDepot(gardeId, parentId);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PATCH
    @Path("/garde/{id}/reprise")
    public Response validerReprise(@HeaderParam("parent-id") Long parentId, @PathParam("id") Long gardeId) {
        try {
            parentService.validerReprise(gardeId, parentId);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/imprevus")
    public Response declarerImprevu(@HeaderParam("parent-id") Long parentId, Garde imprevu) {
        try {
            Garde newImprevu = parentService.declarerImprevu(imprevu, parentId);
            return Response.status(Response.Status.CREATED).entity(newImprevu).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // 2.4. Gestion des absences et congés
    @GET
    @Path("/absences")
    public Response getAbsences(@HeaderParam("parent-id") Long parentId) {
        List<Absence> absences = parentService.getAbsences(parentId);
        return Response.ok(absences).build();
    }

    @POST
    @Path("/absences")
    public Response declarerAbsence(@HeaderParam("parent-id") Long parentId, Absence absence) {
        try {
            Absence newAbsence = parentService.declarerAbsence(absence, parentId);
            return Response.status(Response.Status.CREATED).entity(newAbsence).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/conges")
    public Response demanderConge(@HeaderParam("parent-id") Long parentId, Conge conge) {
        try {
            Conge newConge = parentService.demanderConge(conge, parentId);
            return Response.status(Response.Status.CREATED).entity(newConge).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // 2.5. Gestion des rapports et réclamations
    @GET
    @Path("/rapports")
    public Response getRapports(@HeaderParam("parent-id") Long parentId) {
        List<RapportMensuel> rapports = parentService.getRapports(parentId);
        return Response.ok(rapports).build();
    }

    @GET
    @Path("/rapports/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadRapport(@HeaderParam("parent-id") Long parentId, @QueryParam("rapportId") Long rapportId) {
        try {
            byte[] rapportData = parentService.downloadRapport(rapportId, parentId);
            return Response.ok(rapportData)
                    .header("Content-Disposition", "attachment; filename=rapport.pdf")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (UnsupportedOperationException e) {
            return Response.status(Response.Status.NOT_IMPLEMENTED).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/reclamation-heures")
    public Response reclamerHeures(@HeaderParam("parent-id") Long parentId, Reclamation reclamation) {
        try {
            Reclamation newReclamation = parentService.reclamerHeures(reclamation, parentId);
            return Response.status(Response.Status.CREATED).entity(newReclamation).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}

class LoginRequest {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
