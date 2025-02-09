package com.nounou.times.resources;

import com.nounou.times.model.Garde;
import com.nounou.times.services.GardeService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/gardes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GardeResource {
    @Inject
    GardeService gardeService;

    @GET
    public List<Garde> getAll() {
        return gardeService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Garde garde = gardeService.findById(id);
        if (garde == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(garde).build();
    }

    @POST
    public Response create(Garde garde) {
        gardeService.save(garde);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Garde garde) {
        Garde existing = gardeService.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        gardeService.update(garde);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        Garde garde = gardeService.findById(id);
        if (garde == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        gardeService.delete(id);
        return Response.noContent().build();
    }
}

