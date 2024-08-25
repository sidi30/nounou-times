package com.nounou.times.resources;

import com.nounou.times.model.Report;
import com.nounou.times.services.ReportService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportResource {

    @Inject
    ReportService reportService;

    @GET
    @Path("/parent/{parentId}")
    public List<Report> getReportsByParentId(@PathParam("parentId") Long parentId) {
        return reportService.getReportsByParentId(parentId);
    }

    @POST
    public Response createReport(Report report) {
        Report createdReport = reportService.createReport(report);
        return Response.status(Response.Status.CREATED).entity(createdReport).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateReport(@PathParam("id") Long id, Report report) {
        Report updatedReport = reportService.updateReport(id, report);
        if (updatedReport != null) {
            return Response.ok(updatedReport).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteReport(@PathParam("id") Long id) {
        boolean deleted = reportService.deleteReport(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
