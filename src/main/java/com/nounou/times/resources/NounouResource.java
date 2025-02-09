package com.nounou.times.resources;

import com.nounou.times.model.Nounou;
import com.nounou.times.services.NounouService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/nounous")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NounouResource {
    @Inject
    NounouService nounouService;

    @GET
    public List<Nounou> getAll() {
        return nounouService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Nounou nounou = nounouService.findById(id);
        if (nounou == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(nounou).build();
    }

    @POST
    public Response create(Nounou nounou) {
        nounouService.save(nounou);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Nounou nounou) {
        Nounou existing = nounouService.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        nounouService.update(nounou);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        Nounou nounou = nounouService.findById(id);
        if (nounou == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        nounouService.delete(id);
        return Response.noContent().build();
    }
}
