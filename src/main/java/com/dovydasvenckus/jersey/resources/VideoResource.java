package com.dovydasvenckus.jersey.resources;

import com.dovydasvenckus.jersey.VideoService;
import com.dovydasvenckus.jersey.Video;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/videos")
public class VideoResource {

    private final VideoService videoService = new VideoService();

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVideo(@PathParam("id") int id) {
        Video video = videoService.getVideoById(id);
        if (video != null) {
            return Response.ok(video).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Video not found").build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addVideo(Video video) {
        boolean success = videoService.addVideo(video);
        if (success) {
            return Response.ok(video).status(Response.Status.CREATED).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error adding video").build();
        }
    }
}
