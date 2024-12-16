package com.dovydasvenckus.jersey.resources;

import com.dovydasvenckus.jersey.GoogleCloudStorageService;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Storage.SignUrlOption;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Path("/upload")
public class UploadResource {

    private final GoogleCloudStorageService storage ;
    private final String bucketName = "bucket-movies-project";

    public UploadResource() throws IOException {
        this.storage = new GoogleCloudStorageService();
    }

    @GET
    @Path("/signed-url")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateSignedUrl(@QueryParam("fileName") String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("File name is required").build();
        }

        //BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, "mp4/" + fileName).build();

        String signedUrl = storage.generateSignedUrl(bucketName, "mp4/" + fileName);
        System.out.println("{\"url\": \"" + signedUrl + "\"}");
        return Response.ok("{\"url\": \"" + signedUrl + "\"}").build();
    }
}
