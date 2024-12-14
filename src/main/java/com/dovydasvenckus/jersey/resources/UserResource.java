package com.dovydasvenckus.jersey.resources;

import com.dovydasvenckus.jersey.models.User;
import com.dovydasvenckus.jersey.services.UserService;

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
        boolean isAdded = userService.addUser(user);
        if (isAdded) {
            return Response.status(Response.Status.CREATED)
                    .entity("User added successfully")
                    .build();
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("Failed to add user")
                .build();
    }

    @DELETE
    @Path("/{adminId}/delete/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteUserById(@PathParam("adminId") int adminId, @PathParam("id") int id) {
        boolean isDeleted = userService.deleteUserById(adminId, id);
        if (isDeleted) {
            return Response.ok("User deleted successfully.").build();
        }
        return Response.status(Response.Status.FORBIDDEN)
                .entity("Failed to delete user. Ensure you are an admin with valid credentials and the target is not another admin.")
                .build();
    }

    @DELETE
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUserByEmailAndPassword(@QueryParam("email") String email,
                                                 @QueryParam("password") String password) {
        boolean deleted = userService.deleteUserByEmailAndPassword(email, password);
        if (deleted) {
            return Response.ok("User deleted successfully").build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Invalid email or password, or user not found").build();
    }

    /**
     * Login for CMS application (admin-only)
     */
    @POST
    @Path("/cms/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginToCMS(@QueryParam("email") String email, @QueryParam("password") String password) {
        User loginSuccess = userService.loginToCMS(email, password);
        if (loginSuccess != null) {
            return Response.ok(loginSuccess).build();
        }
        return Response.status(Response.Status.FORBIDDEN).entity("Access denied. Invalid credentials or not an admin.").build();
    }

    /**
     * Login for the main application (any user)
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@QueryParam("email") String email, @QueryParam("password") String password) {
        User loginSuccess = userService.login(email, password);
        if (loginSuccess != null) {
            return Response.ok(loginSuccess).build();
        }
        return Response.status(Response.Status.FORBIDDEN).entity("Invalid email or password.").build();
    }

    /**
     * Endpoint for updating the user's password.
     * A user can only update their own password, and they must be logged in.
     */
    @PUT
    @Path("/update-password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updatePassword(@QueryParam("email") String email,
                                   @QueryParam("oldPassword") String oldPassword,
                                   @QueryParam("newPassword") String newPassword) {
        boolean isUpdated = userService.updatePassword(email, oldPassword, newPassword);
        if (isUpdated) {
            return Response.ok("Password updated successfully.").build();
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("Password update failed. Please check your credentials.")
                .build();
    }


    /**
     * Logout for the user CMS or main
     */
    /**
     * Logout for the user (CMS or main application)
     */
    @POST
    @Path("/logout")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response logout(@QueryParam("email") String email) {
        boolean logoutSuccess = userService.logout(email);
        if (logoutSuccess) {
            return Response.ok("Logout successful. See you next time!").build();
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("Logout failed. User might not be logged in or does not exist.")
                .build();
    }

}
