package com.nounou.times.resources;


import com.nounou.times.model.Absence;
import com.nounou.times.services.AbsenceService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/absences")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AbsenceResource {

    @Inject
    AbsenceService absenceService;

    @GET
    @Path("/child/{childId}")
    public List<Absence> getAbsencesByChildId(@PathParam("childId") Long childId) {
        return absenceService.getAbsencesByChildId(childId);
    }

    @POST
    public Response createAbsence(Absence absence) {
        Absence createdAbsence = absenceService.createAbsence(1L,2L, absence);
        return Response.status(Response.Status.CREATED).entity(createdAbsence).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateAbsence(@PathParam("id") Long id, Absence absence) {
        Absence updatedAbsence = absenceService.updateAbsence(id, absence);
        if (updatedAbsence != null) {
            return Response.ok(updatedAbsence).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteAbsence(@PathParam("id") Long id) {
        boolean deleted = absenceService.deleteAbsence(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
