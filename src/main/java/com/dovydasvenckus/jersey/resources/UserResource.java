package com.dovydasvenckus.jersey.resources;

import com.dovydasvenckus.jersey.User;
import com.dovydasvenckus.jersey.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/users")
public class UserResource {

    private final UserService userService = new UserService();

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (!users.isEmpty()) {
            return Response.ok(users).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("No users found").build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("id") int id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return Response.ok(user).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUser(User user) {
        boolean added = userService.addUser(user);
        if (added) {
            return Response.ok("User added successfully").build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to add user").build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") int id) {
        boolean deleted = userService.deleteUserById(id);
        if (deleted) {
            return Response.ok("User deleted successfully").build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("User not deleted").build();
    }

    /**
     * Login for CMS application (admin-only)
     */
    @POST
    @Path("/cms/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response loginToCMS(@QueryParam("email") String email, @QueryParam("password") String password) {
        boolean loginSuccess = userService.loginToCMS(email, password);
        if (loginSuccess) {
            return Response.ok("Admin login successful. Welcome to the CMS.").build();
        }
        return Response.status(Response.Status.FORBIDDEN).entity("Access denied. Invalid credentials or not an admin.").build();
    }

    /**
     * Login for the main application (any user)
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(@QueryParam("email") String email, @QueryParam("password") String password) {
        boolean loginSuccess = userService.login(email, password);
        if (loginSuccess) {
            return Response.ok("Login successful. Welcome to the application.").build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid email or password.").build();
    }
}
