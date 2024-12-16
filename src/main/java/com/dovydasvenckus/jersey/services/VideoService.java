package com.dovydasvenckus.jersey.services;

import com.dovydasvenckus.jersey.DatabaseConfig;
import com.dovydasvenckus.jersey.GoogleCloudStorageService;
import com.dovydasvenckus.jersey.models.Video;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VideoService {


    private final GoogleCloudStorageService storageService;
    private final String bucketName = "bucket-movies-project";




    public VideoService() throws IOException {
        System.out.println("initialize video service");
        this.storageService = new GoogleCloudStorageService();
    }


    public String getOriginalM3U8Playlist(String bucketName, String m3u8FilePath) throws IOException {
        return storageService.getFileContent(bucketName, m3u8FilePath);
    }


    public String modifyM3U8PlaylistWithSignedUrls(String bucketName, String m3u8FilePath) throws IOException {
        // Fetch the original M3U8 playlist
        String m3u8Playlist = getOriginalM3U8Playlist(bucketName, m3u8FilePath);

        // Extract .ts file paths from the playlist
        List<String> tsFilePaths = extractTsFilePathsFromPlaylist(m3u8Playlist);

        // Generate signed URLs for each .ts file
        List<String> signedUrls = new ArrayList<>();
        String pathPrefix = m3u8FilePath.replace("1080p.m3u8", ""); // dealing with this only, change later
        for (String tsFilePath : tsFilePaths) {
            String signedUrl = storageService.generateSignedUrl(bucketName, pathPrefix + tsFilePath);
            signedUrls.add(signedUrl);
        }

        // Replace .ts file paths in the playlist with the signed URLs
        return replaceTsFilePathsWithSignedUrls(m3u8Playlist, tsFilePaths, signedUrls);
    }

    public void uploadModifiedPlaylistToCloud(String bucketName, String modifiedPlaylist, String m3u8FilePath) throws IOException {
        // Upload the modified playlist to Cloud Storage
        storageService.uploadFile(bucketName, m3u8FilePath, modifiedPlaylist);
    }

    public String generateSignedUrlForModifiedPlaylist(String bucketName, String m3u8FilePath) throws IOException {
        return storageService.generateSignedUrl(bucketName, m3u8FilePath);
    }



    private List<String> extractTsFilePathsFromPlaylist(String m3u8Playlist) {
        List<String> tsFilePaths = new ArrayList<>();
        String[] lines = m3u8Playlist.split("\n");

        for (String line : lines) {
            if (line.endsWith(".ts")) {
                // Caso seja apenas o caminho simples, ex: 1080p_000.ts
                tsFilePaths.add(line);
            } else if (line.startsWith("http")) {
                // Caso seja um URL completo com parâmetros
                String tsFileName = line.substring(line.lastIndexOf("/") + 1); // Extraia o nome
                if (tsFileName.contains("?")) {
                    // Remova parâmetros (tudo após '?')
                    tsFileName = tsFileName.substring(0, tsFileName.indexOf("?"));
                }
                tsFilePaths.add(tsFileName); // Adicione o nome do arquivo .ts
            }
        }

        return tsFilePaths;
    }





    private String replaceTsFilePathsWithSignedUrls(String m3u8Playlist, List<String> tsFilePaths, List<String> signedUrls) {
        for (int i = 0; i < tsFilePaths.size(); i++) {
            m3u8Playlist = m3u8Playlist.replace(tsFilePaths.get(i), signedUrls.get(i));
        }
        return m3u8Playlist;
    }



    public String getSignedMasterUrl(int videoId) {
        String query = "SELECT link_1080p FROM movies WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, videoId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Fetch the master.m3u8 path from the database
                String masterPlaylistPath = rs.getString("link_1080p");

                // Modify the master.m3u8 playlist with signed URLs for the .ts files
                String modifiedPlaylist = modifyM3U8PlaylistWithSignedUrls(bucketName, masterPlaylistPath);


                // Upload the modified playlist back to the cloud storage
                uploadModifiedPlaylistToCloud(bucketName, modifiedPlaylist, masterPlaylistPath);

                // Generate the signed URL for master.m3u8
                //return storageService.generateSignedUrl(bucketName, masterPlaylistPath);
                return generateSignedUrlForModifiedPlaylist(bucketName, masterPlaylistPath);
            } else {
                throw new RuntimeException("Video not found with ID: " + videoId);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching signed master URL");
        }
    }



    /**
     * To get all the videos from the database
     */
    public List<Video> getAllVideos() {
        String query = "SELECT * FROM movies";
        List<Video> videos = new ArrayList<>();
        System.out.println("get all videos");
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
        System.out.println("get a video");
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