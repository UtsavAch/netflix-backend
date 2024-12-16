package com.dovydasvenckus.jersey.resources;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.dovydasvenckus.jersey.services.VideoProcessingService;
import com.dovydasvenckus.jersey.services.VideoService;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/process")
public class VideoProcessingResource {

    private final String bucketName = "bucket-movies-project";
    private final String ffmpegPath = "/usr/bin/ffmpeg";

    private VideoProcessingService videoProcessingService;

    public VideoProcessingResource(){
        try {
            System.out.println("try initialize VideoProcessingService");
            this.videoProcessingService = new VideoProcessingService();
        } catch (IOException e) {
            System.out.println("error initialize VideoProcessingService");
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize VideoProcessingService", e);
        }
    }

    @POST
    @Path("/{fileName}")
    public Response processVideo(@PathParam("fileName") String fileName) {
        try {

            videoProcessingService.processAndUploadVideo(fileName, bucketName);

            return Response.ok("GOOD").build();

        } catch (Exception e) {

            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("ERROr").build();

        }
    }
}
