package com.nounou.times.resources;

import com.nounou.times.model.Overtime;
import com.nounou.times.services.OvertimeService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/overtimes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OvertimeResource {

    @Inject
    OvertimeService overtimeService;

    @GET
    @Path("/schedule/{scheduleId}")
    public List<Overtime> getOvertimesByScheduleId(@PathParam("scheduleId") Long scheduleId) {
        return overtimeService.getOvertimesByScheduleId(scheduleId);
    }

    @POST
    public Response createOvertime(Overtime overtime) {
        Overtime createdOvertime = overtimeService.createOvertime(1L,overtime);
        return Response.status(Response.Status.CREATED).entity(createdOvertime).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateOvertime(@PathParam("id") Long id, Overtime overtime) {
        Overtime updatedOvertime = overtimeService.updateOvertime(id, overtime);
        if (updatedOvertime != null) {
            return Response.ok(updatedOvertime).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteOvertime(@PathParam("id") Long id) {
        boolean deleted = overtimeService.deleteOvertime(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
