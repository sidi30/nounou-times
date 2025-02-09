package com.nounou.times.resources;

import com.nounou.times.model.Evenement;
import com.nounou.times.services.EvenementService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;


@Path("/evenements")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EvenementResource {
    @Inject
    EvenementService evenementService;

    @GET
    public List<Evenement> getAll() {
        return evenementService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Evenement evenement = evenementService.findById(id);
        if (evenement == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(evenement).build();
    }

    @POST
    public Response create(Evenement evenement) {
        evenementService.save(evenement);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Evenement evenement) {
        Evenement existing = evenementService.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        evenement.setId(id);
        evenementService.update(evenement);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        Evenement evenement = evenementService.findById(id);
        if (evenement == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        evenementService.delete(id);
        return Response.noContent().build();
    }
}