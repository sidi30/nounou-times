package com.nounou.times.resources;

import com.nounou.times.model.Nounou;
import com.nounou.times.model.Enfant;
import com.nounou.times.model.Absence;
import com.nounou.times.model.Garde;
import com.nounou.times.model.Evenement;
import com.nounou.times.model.Rapport;
import com.nounou.times.model.FicheDePaie;
import com.nounou.times.services.NounouService;
import com.nounou.times.services.EnfantService;
import com.nounou.times.services.AbsenceService;
import com.nounou.times.services.GardeService;
import com.nounou.times.services.EvenementService;
import com.nounou.times.services.RapportService;
import com.nounou.times.services.FicheDePaieService;
import com.nounou.times.dto.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Path("/api/nounous")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NounouResource {
    @Inject
    NounouService nounouService;

    @Inject
    EnfantService enfantService;

    @Inject
    AbsenceService absenceService;

    @Inject
    GardeService gardeService;

    @Inject
    EvenementService evenementService;

    @Inject
    RapportService rapportService;

    @Inject
    FicheDePaieService ficheDePaieService;

    @POST
    @Path("/signup")
    public Response signup(SignupRequest request) {
        try {
            Nounou nounou = nounouService.signup(request);
            String token = nounouService.generateToken(nounou);
            AuthResponse response = new AuthResponse(token, nounou);
            return Response.status(Response.Status.CREATED)
                         .entity(response)
                         .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(new ErrorResponse(e.getMessage()))
                         .build();
        }
    }

    @POST
    @Path("/signup/page-emploi")
    public Response signupPageEmploi(SignupRequest request) {
        try {
            if (request.getPageEmploiId() == null || request.getPageEmploiId().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                             .entity(new ErrorResponse("L'identifiant Page Emploi est requis"))
                             .build();
            }
            Nounou nounou = nounouService.signupWithPageEmploi(request);
            String token = nounouService.generateToken(nounou);
            AuthResponse response = new AuthResponse(token, nounou);
            return Response.status(Response.Status.CREATED)
                         .entity(response)
                         .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(new ErrorResponse(e.getMessage()))
                         .build();
        }
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        try {
            Nounou nounou = nounouService.login(request.getEmail(), request.getPassword());
            if (nounou == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                             .entity(new ErrorResponse("Email ou mot de passe incorrect"))
                             .build();
            }
            String token = nounouService.generateToken(nounou);
            AuthResponse response = new AuthResponse(token, nounou);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(new ErrorResponse("Erreur lors de la connexion"))
                         .build();
        }
    }

    @GET
    public List<Nounou> getAll() {
        return nounouService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Nounou nounou = nounouService.findById(id);
        if (nounou == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(nounou).build();
    }

    @POST
    public Response create(Nounou nounou) {
        nounouService.save(nounou);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Nounou nounou) {
        Nounou existing = nounouService.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        nounouService.update(nounou);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        Nounou nounou = nounouService.findById(id);
        if (nounou == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        nounouService.delete(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/enfants")
    public Response getEnfants(@HeaderParam("Authorization") String token) {
        if (!nounouService.isValidToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                         .entity(new ErrorResponse("Token invalide"))
                         .build();
        }

        Nounou nounou = nounouService.findByToken(token);
        List<Enfant> enfants = enfantService.findByNounou(nounou.getId());
        return Response.ok(enfants).build();
    }

    @POST
    @Path("/enfants/invitation")
    public Response inviterEnfant(@HeaderParam("Authorization") String token, InvitationRequest request) {
        if (!nounouService.isValidToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                         .entity(new ErrorResponse("Token invalide"))
                         .build();
        }

        try {
            Nounou nounou = nounouService.findByToken(token);
            enfantService.envoyerInvitation(nounou.getId(), request);
            return Response.status(Response.Status.CREATED)
                         .entity(new SuccessResponse("Invitation envoyée avec succès"))
                         .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(new ErrorResponse(e.getMessage()))
                         .build();
        }
    }

    @GET
    @Path("/garde/jour")
    public Response getGardeJour(@HeaderParam("Authorization") String token) {
        if (!nounouService.isValidToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                         .entity(new ErrorResponse("Token invalide"))
                         .build();
        }

        Nounou nounou = nounouService.findByToken(token);
        List<Garde> gardesJour = gardeService.getGardesDuJour(nounou.getId());
        return Response.ok(gardesJour).build();
    }

    @GET
    @Path("/garde/historique")
    public Response getHistoriqueGardes(
            @HeaderParam("Authorization") String token,
            @QueryParam("debut") String dateDebut,
            @QueryParam("fin") String dateFin) {
        if (!nounouService.isValidToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                         .entity(new ErrorResponse("Token invalide"))
                         .build();
        }

        Nounou nounou = nounouService.findByToken(token);
        List<Garde> historique = gardeService.getHistorique(nounou.getId(), 
            dateDebut != null ? LocalDate.parse(dateDebut) : null,
            dateFin != null ? LocalDate.parse(dateFin) : null);
        return Response.ok(historique).build();
    }

    @POST
    @Path("/absences")
    public Response declarerAbsence(@HeaderParam("Authorization") String token, AbsenceRequest request) {
        if (!nounouService.isValidToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                         .entity(new ErrorResponse("Token invalide"))
                         .build();
        }

        try {
            Nounou nounou = nounouService.findByToken(token);
            Absence absence = absenceService.declarer(nounou.getId(), request);
            return Response.status(Response.Status.CREATED)
                         .entity(absence)
                         .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(new ErrorResponse(e.getMessage()))
                         .build();
        }
    }

    @GET
    @Path("/absences")
    public Response getAbsences(
            @HeaderParam("Authorization") String token,
            @QueryParam("debut") String dateDebut,
            @QueryParam("fin") String dateFin) {
        if (!nounouService.isValidToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                         .entity(new ErrorResponse("Token invalide"))
                         .build();
        }

        Nounou nounou = nounouService.findByToken(token);
        List<Absence> absences = absenceService.findByNounou(nounou.getId(),
            dateDebut != null ? LocalDate.parse(dateDebut) : null,
            dateFin != null ? LocalDate.parse(dateFin) : null);
        return Response.ok(absences).build();
    }

    @GET
    @Path("/evenements")
    public Response getEvenements(
            @HeaderParam("Authorization") String token,
            @QueryParam("debut") String dateDebut,
            @QueryParam("fin") String dateFin) {
        if (!nounouService.isValidToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                         .entity(new ErrorResponse("Token invalide"))
                         .build();
        }

        Nounou nounou = nounouService.findByToken(token);
        List<Evenement> evenements = evenementService.findByNounou(nounou.getId(),
            dateDebut != null ? LocalDate.parse(dateDebut) : null,
            dateFin != null ? LocalDate.parse(dateFin) : null);
        return Response.ok(evenements).build();
    }

    @POST
    @Path("/imprevus")
    public Response signalerImprevu(@HeaderParam("Authorization") String token, ImprevisRequest request) {
        if (!nounouService.isValidToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                         .entity(new ErrorResponse("Token invalide"))
                         .build();
        }

        try {
            Nounou nounou = nounouService.findByToken(token);
            Evenement imprevu = evenementService.creerImprevu(nounou.getId(), request);
            return Response.status(Response.Status.CREATED)
                         .entity(imprevu)
                         .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(new ErrorResponse(e.getMessage()))
                         .build();
        }
    }

    @PATCH
    @Path("/evenements/{id}/accept")
    public Response accepterEvenement(
            @HeaderParam("Authorization") String token,
            @PathParam("id") Long evenementId) {
        if (!nounouService.isValidToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                         .entity(new ErrorResponse("Token invalide"))
                         .build();
        }

        try {
            Nounou nounou = nounouService.findByToken(token);
            evenementService.accepterEvenement(nounou.getId(), evenementId);
            return Response.ok(new SuccessResponse("Événement accepté")).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(new ErrorResponse(e.getMessage()))
                         .build();
        }
    }

    @PATCH
    @Path("/evenements/{id}/refuse")
    public Response refuserEvenement(
            @HeaderParam("Authorization") String token,
            @PathParam("id") Long evenementId) {
        if (!nounouService.isValidToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                         .entity(new ErrorResponse("Token invalide"))
                         .build();
        }

        try {
            Nounou nounou = nounouService.findByToken(token);
            evenementService.refuserEvenement(nounou.getId(), evenementId);
            return Response.ok(new SuccessResponse("Événement refusé")).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(new ErrorResponse(e.getMessage()))
                         .build();
        }
    }

    @PATCH
    @Path("/evenements/{id}/reprogrammer")
    public Response reprogrammerEvenement(
            @HeaderParam("Authorization") String token,
            @PathParam("id") Long evenementId,
            ReprogrammationRequest request) {
        if (!nounouService.isValidToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                         .entity(new ErrorResponse("Token invalide"))
                         .build();
        }

        try {
            Nounou nounou = nounouService.findByToken(token);
            evenementService.reprogrammerEvenement(nounou.getId(), evenementId, request);
            return Response.ok(new SuccessResponse("Événement reprogrammé")).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(new ErrorResponse(e.getMessage()))
                         .build();
        }
    }

    @GET
    @Path("/rapports")
    public Response getRapports(
            @HeaderParam("Authorization") String token,
            @QueryParam("annee") Integer annee,
            @QueryParam("mois") Integer mois) {
        if (!nounouService.isValidToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                         .entity(new ErrorResponse("Token invalide"))
                         .build();
        }

        try {
            Nounou nounou = nounouService.findByToken(token);
            YearMonth periode = YearMonth.of(
                annee != null ? annee : YearMonth.now().getYear(),
                mois != null ? mois : YearMonth.now().getMonthValue()
            );
            List<Rapport> rapports = rapportService.getRapportsMensuels(nounou.getId(), periode);
            return Response.ok(rapports).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(new ErrorResponse(e.getMessage()))
                         .build();
        }
    }

    @GET
    @Path("/rapports/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadRapport(
            @HeaderParam("Authorization") String token,
            @QueryParam("rapportId") Long rapportId) {
        if (!nounouService.isValidToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                         .entity(new ErrorResponse("Token invalide"))
                         .build();
        }

        try {
            Nounou nounou = nounouService.findByToken(token);
            StreamingOutput fileStream = rapportService.downloadRapport(nounou.getId(), rapportId);
            return Response.ok(fileStream)
                         .header("Content-Disposition", "attachment; filename=rapport.pdf")
                         .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(new ErrorResponse(e.getMessage()))
                         .build();
        }
    }

    @GET
    @Path("/fiche-paie")
    public Response getFichePaie(
            @HeaderParam("Authorization") String token,
            @QueryParam("annee") Integer annee,
            @QueryParam("mois") Integer mois) {
        if (!nounouService.isValidToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                         .entity(new ErrorResponse("Token invalide"))
                         .build();
        }

        try {
            Nounou nounou = nounouService.findByToken(token);
            YearMonth periode = YearMonth.of(
                annee != null ? annee : YearMonth.now().getYear(),
                mois != null ? mois : YearMonth.now().getMonthValue()
            );
            FicheDePaie fichePaie = ficheDePaieService.genererOuRecuperer(nounou.getId(), periode);
            return Response.ok(fichePaie).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(new ErrorResponse(e.getMessage()))
                         .build();
        }
    }
}
