package com.nounou.times.resources;

import com.nounou.times.model.Child;
import com.nounou.times.services.ChildService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/children")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChildResource {

    @Inject
    ChildService childService;

    @GET
    @Path("/parent/{parentId}")
    public List<Child> getChildrenByParentId(@PathParam("parentId") Long parentId) {
        return childService.getAllChildrenByParent(parentId);
    }

    @GET
    @Path("/{id}")
    public Response getChildById(@PathParam("id") Long id) {
        Child child = childService.getChildById(id);
        if (child != null) {
            return Response.ok(child).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response createChild(Child child) {
        Child createdChild = childService.createChild(1L,child);
        return Response.status(Response.Status.CREATED).entity(createdChild).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateChild(@PathParam("id") Long id, Child child) {
        Child updatedChild = childService.updateChild(id, child);
        if (updatedChild != null) {
            return Response.ok(updatedChild).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteChild(@PathParam("id") Long id) {
        boolean deleted = childService.deleteChild(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
