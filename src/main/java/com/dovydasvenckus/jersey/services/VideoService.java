package com.dovydasvenckus.jersey.services;

import com.dovydasvenckus.jersey.DatabaseConfig;
import com.dovydasvenckus.jersey.GoogleCloudStorageService;
import com.dovydasvenckus.jersey.models.Video;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VideoService {


    private final GoogleCloudStorageService storageService;
    private final String bucketName = "bucket-movies-project";


    private final String jsonCredentials = "{\n" +
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

    public VideoService() throws IOException {
        // Passando o JSON das credenciais diretamente para o serviço
        this.storageService = new GoogleCloudStorageService(jsonCredentials);
    }

    /**
     * To get all the videos from the database
     */
    public List<Video> getAllVideos() {
        String query = "SELECT * FROM movies";
        List<Video> videos = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            // Loop through the result set and map each row to a Video object
            while (rs.next()) {

                String link360pPath = rs.getString("link_360p");
                String link1080pPath = rs.getString("link_1080p");

                // Generate signed URLs
                String signedUrl360p = storageService.generateSignedUrl(bucketName, link360pPath);
                String signedUrl1080p = storageService.generateSignedUrl(bucketName, link1080pPath);

                videos.add(new Video(
                        rs.getInt("id"),                // ID
                        rs.getString("name"),           // Name
                        rs.getString("genre"),          // Genre
                        rs.getInt("duration"),          // Duration in minutes
                        signedUrl360p,      // Link to 360p video
                        signedUrl1080p      // Link to 1080p video
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        }

        return videos; // Return the list of videos (empty if none exist)
    }


    /**
     * To get video from the database by its ID
     */
    public Video getVideoById(int id) {
        String query = "SELECT * FROM movies WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection(); // Get connection from DatabaseConfig
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id); // Set the video ID in the query
            ResultSet rs = stmt.executeQuery();

            // If a result is found, map it to the Video object
            if (rs.next()) {

                String link360pPath = rs.getString("link_360p");
                String link1080pPath = rs.getString("link_1080p");

                // Generate signed URLs
                String signedUrl360p = storageService.generateSignedUrl(bucketName, link360pPath);
                String signedUrl1080p = storageService.generateSignedUrl(bucketName, link1080pPath);

                return new Video(
                        rs.getInt("id"),                // ID
                        rs.getString("name"),           // Name
                        rs.getString("genre"),          // Genre
                        rs.getInt("duration"),          // Duration in minutes
                        signedUrl360p,
                        signedUrl1080p     // Link to 1080p video
                );
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        }
        return null; // Return null if no video is found or an error occurs
    }

    /**
     * To get all videos from the database by their name.
     */
    public List<Video> getVideosByName(String name) {
        String query = "SELECT * FROM movies WHERE name = ?";
        List<Video> videos = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name); // Set the video name in the query
            ResultSet rs = stmt.executeQuery();

            // Loop through the result set and add each matching video to the list
            while (rs.next()) {
                videos.add(new Video(
                        rs.getInt("id"),                // ID
                        rs.getString("name"),           // Name
                        rs.getString("genre"),          // Genre
                        rs.getInt("duration"),          // Duration in minutes
                        rs.getString("link_360p"),      // Link to 360p video
                        rs.getString("link_1080p")      // Link to 1080p video
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        }

        return videos; // Return the list of videos (empty if no matches found)
    }

    /**
     * To get all videos from the database by their genre.
     */
    public List<Video> getVideosByGenre(String genre) {
        String query = "SELECT * FROM movies WHERE genre = ?";
        List<Video> videos = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, genre); // Set the genre in the query
            ResultSet rs = stmt.executeQuery();

            // Loop through the result set and add each matching video to the list
            while (rs.next()) {
                videos.add(new Video(
                        rs.getInt("id"),                // ID
                        rs.getString("name"),           // Name
                        rs.getString("genre"),          // Genre
                        rs.getInt("duration"),          // Duration in minutes
                        rs.getString("link_360p"),      // Link to 360p video
                        rs.getString("link_1080p")      // Link to 1080p video
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        }

        return videos; // Return the list of videos (empty if no matches found)
    }

    // Add a new video to the database

    /**
     * To add a new video in the database
     */

    public boolean addVideo(Video video) {
        String findVacantIdQuery = "SELECT MIN(t1.id + 1) AS vacant_id " +
                "FROM movies t1 " +
                "LEFT JOIN movies t2 ON t1.id + 1 = t2.id " +
                "WHERE t2.id IS NULL";
        String insertVideoQuery = "INSERT INTO movies (id, name, genre, duration, link_360p, link_1080p) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement findVacantStmt = conn.prepareStatement(findVacantIdQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertVideoQuery)) {

            // Step 1: Find the first vacant ID
            int vacantId = -1; // Default value to indicate no vacant ID
            ResultSet rs = findVacantStmt.executeQuery();
            if (rs.next()) {
                vacantId = rs.getInt("vacant_id"); // Get the vacant ID
            }

            // Step 2: Insert the video
            int idToUse = (vacantId > 0) ? vacantId : video.getId();
            insertStmt.setInt(1, idToUse);
            insertStmt.setString(2, video.getName());
            insertStmt.setString(3, video.getGenre());
            insertStmt.setInt(4, video.getDuration());
            insertStmt.setString(5, video.getLink360p());
            insertStmt.setString(6, video.getLink1080p());

            // Execute the insertion
            int rowsInserted = insertStmt.executeUpdate();
            return rowsInserted > 0; // Return true if insertion was successful
        } catch (SQLException e) {
            // Log the exception
            e.printStackTrace();
        }
        return false; // Return false if insertion failed
    }

    /**
     * Delete a video from the database by its ID.
     */
    public boolean deleteVideoById(int id) {
        String deleteQuery = "DELETE FROM movies WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection(); // Get database connection
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setInt(1, id); // Set the video ID in the query

            int rowsDeleted = stmt.executeUpdate(); // Execute the deletion
            return rowsDeleted > 0; // Return true if a row was deleted
        } catch (SQLException e) {
            // Log the exception
            e.printStackTrace();
        }

        return false; // Return false if deletion failed
    }


}