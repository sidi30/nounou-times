package com.nounou.times.security;

import io.quarkus.oidc.IdToken;
import io.quarkus.oidc.RefreshToken;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/oidc")
public class OidcResource {

    @Inject
    @IdToken
    JsonWebToken idToken;

    @Inject
    JsonWebToken accessToken;

    @Inject
    RefreshToken refreshToken;

    @GET
    @Path("/user-info")
    @Authenticated
    @Produces(MediaType.APPLICATION_JSON)
    public UserInfo getUserInfo() {
        return new UserInfo(
            idToken.getName(),
            idToken.getClaim("email"),
            accessToken.getGroups()
        );
    }

    public static class UserInfo {
        public String name;
        public String email;
        public Set<String> roles;

        public UserInfo(String name, String email, Set<String> roles) {
            this.name = name;
            this.email = email;
            this.roles = roles;
        }
    }
}
