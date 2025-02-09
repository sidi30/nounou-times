package com.nounou.times.resources;

import com.nounou.times.model.FicheDePaie;
import com.nounou.times.services.FicheDePaieService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/fiches-de-paie")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FicheDePaieResource {
    @Inject
    FicheDePaieService ficheDePaieService;

    @GET
    public List<FicheDePaie> getAll() {
        return ficheDePaieService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        FicheDePaie ficheDePaie = ficheDePaieService.findById(id);
        if (ficheDePaie == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(ficheDePaie).build();
    }

    @POST
    public Response create(FicheDePaie ficheDePaie) {
        ficheDePaieService.save(ficheDePaie);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, FicheDePaie ficheDePaie) {
        FicheDePaie existing = ficheDePaieService.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ficheDePaie.setId(id);
        ficheDePaieService.update(ficheDePaie);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        FicheDePaie ficheDePaie = ficheDePaieService.findById(id);
        if (ficheDePaie == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ficheDePaieService.delete(id);
        return Response.noContent().build();
    }
}