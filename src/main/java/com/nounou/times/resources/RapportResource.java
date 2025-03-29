package com.nounou.times.resources;

import com.nounou.times.model.GenerationRequest;
import com.nounou.times.model.RapportMensuel;
import com.nounou.times.services.RapportService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;
import java.util.List;

@Path("/api/rapports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RapportResource {
    @Inject
    RapportService rapportService;

    @GET
    public Response getRapports(
            @HeaderParam("nounou-id") Long nounouId,
            @QueryParam("annee") Integer annee,
            @QueryParam("mois") Integer mois) {
        try {
            if (annee == null || mois == null) {
                YearMonth maintenant = YearMonth.now();
                annee = maintenant.getYear();
                mois = maintenant.getMonthValue();
            }
            
            YearMonth periode = YearMonth.of(annee, mois);
            List<RapportMensuel> rapports = rapportService.getRapportsMensuels(nounouId, periode);
            return Response.ok(rapports).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}/telecharger")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response telechargerRapport(
            @HeaderParam("nounou-id") Long nounouId,
            @PathParam("id") Long rapportId) {
        try {
            StreamingOutput streamingOutput = rapportService.downloadRapport(nounouId, rapportId);
            return Response.ok(streamingOutput)
                    .header("Content-Disposition", "attachment; filename=rapport-mensuel.xlsx")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/generer")
    public Response genererRapport(
            @HeaderParam("nounou-id") Long nounouId,
            GenerationRequest request) {
        try {
            YearMonth periode = YearMonth.of(request.getAnnee(), request.getMois());
            List<RapportMensuel> rapports = rapportService.getRapportsMensuels(nounouId, periode);
            return Response.status(Response.Status.CREATED)
                    .entity(rapports)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
}

