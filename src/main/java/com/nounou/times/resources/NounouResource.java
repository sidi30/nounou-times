package com.nounou.times.resources;

import com.nounou.times.dto.*;
import com.nounou.times.model.*;
import com.nounou.times.services.*;
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

    @Inject NounouService nounouService;
    @Inject EnfantService enfantService;
    @Inject AbsenceService absenceService;
    @Inject GardeService gardeService;
    @Inject EvenementService evenementService;
    @Inject RapportService rapportService;
    @Inject FicheDePaieService ficheDePaieService;

    // ─── Auth ─────────────────────────────────────────────────────────────────

    @POST
    @Path("/signup")
    public Response signup(SignupRequest request) {
        try {
            Nounou nounou = nounouService.signup(request);
            return Response.status(Response.Status.CREATED)
                    .entity(new AuthResponse(nounouService.generateToken(nounou), nounou))
                    .build();
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    @POST
    @Path("/signup/page-emploi")
    public Response signupPageEmploi(SignupRequest request) {
        try {
            Nounou nounou = nounouService.signupWithPageEmploi(request);
            return Response.status(Response.Status.CREATED)
                    .entity(new AuthResponse(nounouService.generateToken(nounou), nounou))
                    .build();
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        return nounouService.login(request.getEmail(), request.getPassword())
                .map(n -> Response.ok(new AuthResponse(nounouService.generateToken(n), n)).build())
                .orElse(Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("Email ou mot de passe incorrect")).build());
    }

    // ─── CRUD de base ─────────────────────────────────────────────────────────

    @GET
    public List<Nounou> getAll() {
        return nounouService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        return nounouService.findById(id)
                .map(n -> Response.ok(n).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response create(Nounou nounou) {
        nounouService.save(nounou);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Nounou nounou) {
        if (nounouService.findById(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        nounouService.update(nounou);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        if (nounouService.findById(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        nounouService.delete(id);
        return Response.noContent().build();
    }

    // ─── Enfants ──────────────────────────────────────────────────────────────

    @GET
    @Path("/enfants")
    public Response getEnfants(@HeaderParam("Authorization") String token) {
        return withNounou(token, nounou -> {
            List<Enfant> enfants = enfantService.findByNounou(nounou.id);
            return Response.ok(enfants).build();
        });
    }

    @POST
    @Path("/enfants/invitation")
    public Response inviterEnfant(@HeaderParam("Authorization") String token, InvitationRequest request) {
        return withNounou(token, nounou -> {
            try {
                enfantService.envoyerInvitation(nounou.id, request);
                return Response.status(Response.Status.CREATED)
                        .entity(new SuccessResponse("Invitation envoyée avec succès")).build();
            } catch (IllegalArgumentException e) {
                return badRequest(e.getMessage());
            }
        });
    }

    // ─── Gardes ───────────────────────────────────────────────────────────────

    @GET
    @Path("/garde/jour")
    public Response getGardeJour(@HeaderParam("Authorization") String token) {
        return withNounou(token, nounou ->
                Response.ok(gardeService.getGardesDuJour(nounou.id)).build());
    }

    @GET
    @Path("/garde/historique")
    public Response getHistoriqueGardes(
            @HeaderParam("Authorization") String token,
            @QueryParam("debut") String dateDebut,
            @QueryParam("fin") String dateFin) {
        return withNounou(token, nounou -> {
            List<Garde> historique = gardeService.getHistorique(nounou.id,
                    dateDebut != null ? LocalDate.parse(dateDebut) : null,
                    dateFin != null ? LocalDate.parse(dateFin) : null);
            return Response.ok(historique).build();
        });
    }

    // ─── Absences ─────────────────────────────────────────────────────────────

    @POST
    @Path("/absences")
    public Response declarerAbsence(@HeaderParam("Authorization") String token, AbsenceRequest request) {
        return withNounou(token, nounou -> {
            try {
                return Response.status(Response.Status.CREATED)
                        .entity(absenceService.declarer(nounou.id, request)).build();
            } catch (IllegalArgumentException e) {
                return badRequest(e.getMessage());
            }
        });
    }

    @GET
    @Path("/absences")
    public Response getAbsences(
            @HeaderParam("Authorization") String token,
            @QueryParam("debut") String dateDebut,
            @QueryParam("fin") String dateFin) {
        return withNounou(token, nounou -> {
            List<Absence> absences = absenceService.findByNounou(nounou.id,
                    dateDebut != null ? LocalDate.parse(dateDebut) : null,
                    dateFin != null ? LocalDate.parse(dateFin) : null);
            return Response.ok(absences).build();
        });
    }

    // ─── Événements ───────────────────────────────────────────────────────────

    @GET
    @Path("/evenements")
    public Response getEvenements(
            @HeaderParam("Authorization") String token,
            @QueryParam("debut") String dateDebut,
            @QueryParam("fin") String dateFin) {
        return withNounou(token, nounou -> {
            List<Evenement> evenements = evenementService.findByNounou(nounou.id,
                    dateDebut != null ? LocalDate.parse(dateDebut) : null,
                    dateFin != null ? LocalDate.parse(dateFin) : null);
            return Response.ok(evenements).build();
        });
    }

    @POST
    @Path("/imprevus")
    public Response signalerImprevu(@HeaderParam("Authorization") String token, ImprevisRequest request) {
        return withNounou(token, nounou -> {
            try {
                return Response.status(Response.Status.CREATED)
                        .entity(evenementService.creerImprevu(nounou.id, request)).build();
            } catch (IllegalArgumentException e) {
                return badRequest(e.getMessage());
            }
        });
    }

    @PATCH
    @Path("/evenements/{id}/accept")
    public Response accepterEvenement(@HeaderParam("Authorization") String token, @PathParam("id") Long id) {
        return withNounou(token, nounou -> {
            try {
                evenementService.accepterEvenement(nounou.id, id);
                return Response.ok(new SuccessResponse("Événement accepté")).build();
            } catch (IllegalArgumentException e) {
                return badRequest(e.getMessage());
            }
        });
    }

    @PATCH
    @Path("/evenements/{id}/refuse")
    public Response refuserEvenement(@HeaderParam("Authorization") String token, @PathParam("id") Long id) {
        return withNounou(token, nounou -> {
            try {
                evenementService.refuserEvenement(nounou.id, id);
                return Response.ok(new SuccessResponse("Événement refusé")).build();
            } catch (IllegalArgumentException e) {
                return badRequest(e.getMessage());
            }
        });
    }

    @PATCH
    @Path("/evenements/{id}/reprogrammer")
    public Response reprogrammerEvenement(
            @HeaderParam("Authorization") String token,
            @PathParam("id") Long id,
            ReprogrammationRequest request) {
        return withNounou(token, nounou -> {
            try {
                evenementService.reprogrammerEvenement(nounou.id, id, request);
                return Response.ok(new SuccessResponse("Événement reprogrammé")).build();
            } catch (IllegalArgumentException e) {
                return badRequest(e.getMessage());
            }
        });
    }

    // ─── Rapports & Fiches de paie ────────────────────────────────────────────

    @GET
    @Path("/rapports")
    public Response getRapports(
            @HeaderParam("Authorization") String token,
            @QueryParam("annee") Integer annee,
            @QueryParam("mois") Integer mois) {
        return withNounou(token, nounou -> {
            try {
                YearMonth periode = YearMonth.of(
                        annee != null ? annee : YearMonth.now().getYear(),
                        mois != null ? mois : YearMonth.now().getMonthValue());
                return Response.ok(rapportService.getRapportsMensuels(nounou.id, periode)).build();
            } catch (IllegalArgumentException e) {
                return badRequest(e.getMessage());
            }
        });
    }

    @GET
    @Path("/rapports/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadRapport(
            @HeaderParam("Authorization") String token,
            @QueryParam("rapportId") Long rapportId) {
        return withNounou(token, nounou -> {
            try {
                StreamingOutput stream = rapportService.downloadRapport(nounou.id, rapportId);
                return Response.ok(stream)
                        .header("Content-Disposition", "attachment; filename=rapport.xlsx")
                        .build();
            } catch (IllegalArgumentException e) {
                return badRequest(e.getMessage());
            }
        });
    }

    @GET
    @Path("/fiche-paie")
    public Response getFichePaie(
            @HeaderParam("Authorization") String token,
            @QueryParam("annee") Integer annee,
            @QueryParam("mois") Integer mois) {
        return withNounou(token, nounou -> {
            try {
                YearMonth periode = YearMonth.of(
                        annee != null ? annee : YearMonth.now().getYear(),
                        mois != null ? mois : YearMonth.now().getMonthValue());
                return Response.ok(ficheDePaieService.genererOuRecuperer(nounou.id, periode)).build();
            } catch (IllegalArgumentException e) {
                return badRequest(e.getMessage());
            }
        });
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Response withNounou(String token, java.util.function.Function<Nounou, Response> handler) {
        return nounouService.findByToken(token)
                .map(handler)
                .orElse(Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("Token invalide")).build());
    }

    private Response badRequest(String message) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(message)).build();
    }
}
