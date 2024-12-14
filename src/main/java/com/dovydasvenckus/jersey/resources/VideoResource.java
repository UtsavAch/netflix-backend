package com.dovydasvenckus.jersey.resources;

import com.dovydasvenckus.jersey.services.VideoService;
import com.dovydasvenckus.jersey.models.Video;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/videos")
public class VideoResource {

    private final VideoService videoService = new VideoService();

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllVideos() {
        List<Video> videos = videoService.getAllVideos();
        if (!videos.isEmpty()) {
            return Response.ok(videos).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("No videos found").build();
        }
    }

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

    @GET
    @Path("/name/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVideosByName(@PathParam("name") String name) {
        List<Video> videos = videoService.getVideosByName(name);
        if (!videos.isEmpty()) {
            return Response.ok(videos).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No videos found with the name: " + name)
                    .build();
        }
    }

    @GET
    @Path("/genre/{genre}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVideosByGenre(@PathParam("genre") String genre) {
        List<Video> videos = videoService.getVideosByGenre(genre);
        if (!videos.isEmpty()) {
            return Response.ok(videos).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No videos found with the genre: " + genre)
                    .build();
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

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteVideo(@PathParam("id") int id) {
        boolean success = videoService.deleteVideoById(id);
        if (success) {
            return Response.ok().entity("Video with ID " + id + " deleted successfully").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Video not found").build();
        }
    }

}
