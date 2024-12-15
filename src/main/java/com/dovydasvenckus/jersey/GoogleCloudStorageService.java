package com.dovydasvenckus.jersey;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GoogleCloudStorageService {
    private final Storage storage;

    public GoogleCloudStorageService(String jsonCredentials) throws IOException {
        // Load credentials from JSON string
        GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(jsonCredentials.getBytes()));

        // Configure the storage service with credentials
        this.storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId("bucket-movies-project")
                .build()
                .getService();
    }

    // Generate a signed URL for a specific file in the bucket
    public String generateSignedUrl(String bucketName, String filePath) {
        BlobId blobId = BlobId.of(bucketName, filePath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        URL signedUrl = storage.signUrl(blobInfo, 15, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature());
        return signedUrl.toString();
    }

    // Retrieve the content of a file from the bucket
    public String getFileContent(String bucketName, String filePath) throws IOException {
        BlobId blobId = BlobId.of(bucketName, filePath);
        return new String(storage.readAllBytes(blobId));
    }

    public void uploadFile(String bucketName, String filePath, String content) throws IOException {
        // Convert the content string to a byte array
        byte[] contentBytes = content.getBytes("UTF-8");

        // Create a BlobInfo object with the bucket name and file path
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, filePath).build();

        // Upload the content to Cloud Storage
        Blob blob = storage.create(blobInfo, contentBytes);

        // Optionally, print the URL to the uploaded file
        System.out.println("File uploaded to: " + blob.getSelfLink());
    }
}






