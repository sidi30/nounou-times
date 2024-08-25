package com.nounou.times.resources;

import com.nounou.times.model.Schedule;
import com.nounou.times.services.ScheduleService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;

@Path("/schedules")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ScheduleResource {

    @Inject
    ScheduleService scheduleService;

    @GET
    @Path("/child/{childId}")
    public List<Schedule> getSchedulesByChildId(@PathParam("childId") Long childId) {
        return scheduleService.getSchedulesByChildId(childId);
    }

    //

    @GET
    @Path("/parent/{parentId}/nounou/{nounouId}{month}")
    public List<Schedule> getSchedulesByParentIdNounouIdAndMonth(
            @PathParam("parentId") Long parentId,
            @PathParam("nounouId") Long nounouId,
             LocalDate month) {
        return scheduleService.getSchedulesByParentIdNounouIdAndMonth(parentId, nounouId, month);
    }

    @POST
    public Response createSchedule(Schedule schedule) {
        Schedule createdSchedule = scheduleService.createSchedule(1L,1L,1L,  schedule);
        return Response.status(Response.Status.CREATED).entity(createdSchedule).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateSchedule(@PathParam("id") Long id, Schedule schedule) {
        Schedule updatedSchedule = scheduleService.updateSchedule(id, schedule);
        if (updatedSchedule != null) {
            return Response.ok(updatedSchedule).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteSchedule(@PathParam("id") Long id) {
        boolean deleted = scheduleService.deleteSchedule(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
