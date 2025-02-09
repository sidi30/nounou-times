package com.nounou.times.resources;

import com.nounou.times.model.HeuresSupplementaires;
import com.nounou.times.services.HeuresSupplementairesService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/heures-supplementaires")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HeuresSupplementairesResource {
    @Inject
    HeuresSupplementairesService heuresSupplementairesService;

    @GET
    public List<HeuresSupplementaires> getAll() {
        return heuresSupplementairesService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        HeuresSupplementaires heuresSupplementaires = heuresSupplementairesService.findById(id);
        if (heuresSupplementaires == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(heuresSupplementaires).build();
    }

    @POST
    public Response create(HeuresSupplementaires heuresSupplementaires) {
        heuresSupplementairesService.save(heuresSupplementaires);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, HeuresSupplementaires heuresSupplementaires) {
        HeuresSupplementaires existing = heuresSupplementairesService.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        heuresSupplementairesService.update(heuresSupplementaires);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        HeuresSupplementaires heuresSupplementaires = heuresSupplementairesService.findById(id);
        if (heuresSupplementaires == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        heuresSupplementairesService.delete(id);
        return Response.noContent().build();
    }
}