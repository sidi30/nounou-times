package com.nounou.times.resources;

import com.nounou.times.model.RapportMensuel;
import com.nounou.times.services.RapportService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/rapports-mensuels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RapportResource {
    @Inject
    RapportService rapportMensuelService;

    @GET
    public List<RapportMensuel> getAll() {
        return rapportMensuelService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        RapportMensuel rapportMensuel = rapportMensuelService.findById(id);
        if (rapportMensuel == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(rapportMensuel).build();
    }

    @POST
    public Response create(RapportMensuel rapportMensuel) {
        rapportMensuelService.save(rapportMensuel);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, RapportMensuel rapportMensuel) {
        RapportMensuel existing = rapportMensuelService.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        rapportMensuelService.update(rapportMensuel);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        RapportMensuel rapportMensuel = rapportMensuelService.findById(id);
        if (rapportMensuel == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        rapportMensuelService.delete(id);
        return Response.noContent().build();
    }
}