package com.nounou.times.resources;

import com.nounou.times.model.*;
import com.nounou.times.services.ParentService;
import com.nounou.times.services.NounouService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    ParentService parentService;

    @Inject
    NounouService nounouService;

    @POST
    @Path("/parents/signup")
    public Response signupParent(Parent parent) {
        try {
            Parent newParent = parentService.signup(parent);
            return Response.status(Response.Status.CREATED).entity(newParent).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/parents/login")
    public Response loginParent(LoginRequest loginRequest) {
        try {
            Parent parent = parentService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return Response.ok(parent).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/nounous/signup")
    public Response signupNounou(Nounou nounou) {
        try {
            Nounou newNounou = nounouService.signup(nounou);
            return Response.status(Response.Status.CREATED).entity(newNounou).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/nounous/login")
    public Response loginNounou(LoginRequest loginRequest) {
        try {
            Nounou nounou = nounouService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return Response.ok(nounou).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }
}

class LoginRequest {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
