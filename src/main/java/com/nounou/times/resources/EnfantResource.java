package com.nounou.times.resources;

import com.nounou.times.model.Enfant;
import com.nounou.times.services.EnfantService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/enfants")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EnfantResource {
    @Inject
    EnfantService enfantService;

    @GET
    public List<Enfant> getAll() {
        return enfantService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Enfant enfant = enfantService.findById(id);
        if (enfant == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(enfant).build();
    }

    @POST
    public Response create(Enfant enfant) {
        enfantService.save(enfant);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Enfant enfant) {
        Enfant existing = enfantService.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        enfantService.update(enfant);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        Enfant enfant = enfantService.findById(id);
        if (enfant == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        enfantService.delete(id);
        return Response.noContent().build();
    }
}