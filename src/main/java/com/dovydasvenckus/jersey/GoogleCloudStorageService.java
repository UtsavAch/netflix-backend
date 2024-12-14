package com.dovydasvenckus.jersey;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class GoogleCloudStorageService {
    private final Storage storage;

    public GoogleCloudStorageService(String jsonCredentials) throws IOException, IOException {
        // Carregar as credenciais diretamente a partir de uma string JSON
        GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(jsonCredentials.getBytes()));

        // Configurar o serviço de Storage com as credenciais
        this.storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId("your-project-id")  // Certifique-se de substituir pelo seu ID do projeto
                .build()
                .getService();
    }

    /**
     * Generates a signed URL for the given file path in the specified bucket.
     *
     * @param bucketName Name of the bucket
     * @param filePath   Path of the file in the bucket
     * @return Signed URL as a string
     */
    public String generateSignedUrl(String bucketName, String filePath) {
        BlobId blobId = BlobId.of(bucketName, filePath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        // Generate a signed URL valid for 15 minutes
        URL signedUrl = storage.signUrl(blobInfo, 15, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature());
        return signedUrl.toString();
    }
}






