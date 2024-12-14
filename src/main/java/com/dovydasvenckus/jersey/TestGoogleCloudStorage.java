package com.dovydasvenckus.jersey;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TestGoogleCloudStorage {
    public static void main(String[] args) throws IOException {
        String jsonCredentials = "{\n" +
                "  \"type\": \"service_account\",\n" +
                "  \"project_id\": \"projectnetflix-437314\",\n" +
                "  \"private_key_id\": \"dc2e07604bcd56684662b76dcd98d011824d8b19\",\n" +
                "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCpf6jpxGKJGds+\\nFvf5M3sGvIiPU2D8HfeGZPYXE9OOU7Vdqc8gEeMZqEt7FYTM1ltn+RGFUmTOFynX\\njon+scUqrv/rMejuwMP96hLkbmScDsF+EDsiKcdhb4p0mW/OnjBy0OkhQ2C1Ha7l\\npbINy2tReY06CZ7IcWrGUsnhKewHpxAy5JBrS5pQP/9zHUWQhbafg7+xvjGljF4n\\nZjNTRCh3yh5wAh64zI6J0ldG63Q8o2NzKVpbZp/UuoATOzl2zQGAwejH9IHhaiVA\\n/uwqi7q3peaS1+RukLi5T128lMWNGWJhceH8+DfQIR13QwJRjyBwpCwTimfIAx1L\\nTj1iX/SlAgMBAAECggEAC4H81F4+eGN2hKknjdZGcIclWGHHUGaycv/wy1FPyDZ7\\ntk+8ncdJHWlVMpab7RJSo2UAIQaejHV7JETrroygP80g1/WMBIW26MrXHIY4M3Y3\\nAdmVQjUG8eESS+dM9YC89lajtVvvOWy5/qJ4y8H3V9FCWuHjU2WL48okJbLkcQJ0\\npLEoAb0E94zUvV8ktFyZzi8S/G2rSbB408zReOMlCIU/Wa/74jKAZ/OGbw5SNSqU\\n41EduCC/EV7AriEUudKhn6JuiYnELjdhdee1PxvTgB6eHmRkJ0UhmEHuk2zeftyG\\nJyq6MEsKVx770icPddvs6LPU3V6T6xAlnVNf4G+esQKBgQC1rxqKOXzerXhKmyXy\\nLECf5R1EuBl3UtWcajNFBGVZjVaCY5/ujZhoRgarpwyDCcXrmMLi5+0l709vCL6M\\nvJ9zdho22d2LdHaGWZQk9GP+wmgk98ufb0WsdqUzjddFSh6ajgHZOLZdMi7rY0DB\\n08lY03B3cM4NhRxKBt3CHD/hLQKBgQDu1JRGhjWQbGSOxdl1/5h5uo0ThyQKEvq7\\nn+8rFN5o+95QmHYBNcvyzYmKgjnU5HUnp+J68lcWhvxPRXlCfsgf4F0ti3U/yHxQ\\nCVLzPGwblWHCWmDmdWyKcKl74839PgByZ6dN82TFD52fh6Gduw2uuRenVWmhTvOP\\nFykKbkjcWQKBgQCHI16/T7FkxG0EOVDJSEctZ7MUiUdP7Qo8VPYbsQBd0vZ09/te\\n6m1hqiyOAywYT+2qpy7WriJEJDPWaA+sCSUlMcSf5f+XGiKLHhhGQI4cUag7TyFj\\nTtXpSTrqFOf5fv8ygMw5Majbu1cQ+PuS8KAEdQljnkF15vu6yE6scmzFZQKBgQDW\\n4mG+yoZrOXuIki9E4gH4lIbWaSNeBRGfuxjf9FjgsK2oamTSVer4vUHhY6ZRDHT0\\nhzNZV65P0Ig3ctTVpWi+dYqgrfeCugpPoPQHcff7IX7h9Zt1/3T3YsK7e44dKqoQ\\nRX7cvf+O5qv1m30og+KdsF+96TWvM3Ak8Lu2bOAVkQKBgQCogEy0WWqjDZxmT3vq\\n1X/EPVU1P0U9D8K0aYmvo0z8zOeSCVWe1WoaxjkO4MVCwjAFNJKNI0OQYTNCfUvc\\n5MDraUZliH0GSioqlVWmLlda5/HVUQFBH51UpUrgXikI1PaVjuEgykyRDT6vH+dR\\nWrCsDTo1IIprrNlXUnFOGvB4EA==\\n-----END PRIVATE KEY-----\\n\",\n" +
                "  \"client_email\": \"291715051648-compute@developer.gserviceaccount.com\",\n" +
                "  \"client_id\": \"105685468130156421485\",\n" +
                "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/291715051648-compute%40developer.gserviceaccount.com\",\n" +
                "  \"universe_domain\": \"googleapis.com\"\n" +
                "}\n";

        // Carregar as credenciais a partir da string JSON
        GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(jsonCredentials.getBytes()));
        String projectId = "projectnetflix-437314";  // Substitua pelo seu ID de projeto

        // Configure a instância do Storage com o projectId
        Storage storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .build()
                .getService();

        for (Bucket bucket : storage.list().iterateAll()) {
            System.out.println(bucket.getName());
        }
    }
}
