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
    public List<Absence> getAll() {
        return absenceService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Absence absence = absenceService.findById(id);
        if (absence == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(absence).build();
    }

    @POST
    public Response create(Absence absence) {
        absenceService.save(absence);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Absence absence) {
        Absence existing = absenceService.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        absenceService.update(absence);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        Absence absence = absenceService.findById(id);
        if (absence == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        absenceService.delete(String.valueOf(id));
        return Response.noContent().build();
    }
}