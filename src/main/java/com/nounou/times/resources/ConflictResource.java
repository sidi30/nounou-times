package com.nounou.times.resources;

import com.nounou.times.model.Conflict;
import com.nounou.times.services.ConflictService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/conflicts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConflictResource {

    @Inject
    ConflictService conflictService;

    @GET
    @Path("/schedule/{scheduleId}")
    public List<Conflict> getConflictsByScheduleId(@PathParam("scheduleId") Long scheduleId) {
        return conflictService.getConflictsByScheduleId(scheduleId);
    }

    @POST
    public Response createConflict(Conflict conflict) {
        Conflict createdConflict = conflictService.createConflict(conflict);
        return Response.status(Response.Status.CREATED).entity(createdConflict).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateConflict(@PathParam("id") Long id, Conflict conflict) {
        Conflict updatedConflict = conflictService.updateConflict(id, conflict);
        if (updatedConflict != null) {
            return Response.ok(updatedConflict).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteConflict(@PathParam("id") Long id) {
        boolean deleted = conflictService.deleteConflict(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
