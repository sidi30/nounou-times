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
        return heuresSupplementairesService.findById(id)
                .map(h -> Response.ok(h).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response create(HeuresSupplementaires heures) {
        heuresSupplementairesService.save(heures);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, HeuresSupplementaires heures) {
        if (heuresSupplementairesService.findById(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        heuresSupplementairesService.update(heures);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        if (heuresSupplementairesService.findById(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        heuresSupplementairesService.delete(id);
        return Response.noContent().build();
    }
}
