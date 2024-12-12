package com.dovydasvenckus.jersey.services;

import com.dovydasvenckus.jersey.DatabaseConfig;
import com.dovydasvenckus.jersey.models.Video;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VideoService {

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
                return new Video(
                        rs.getInt("id"),                // ID
                        rs.getString("name"),           // Name
                        rs.getString("genre"),          // Genre
                        rs.getInt("duration"),          // Duration in minutes
                        rs.getString("link_360p"),      // Link to 360p video
                        rs.getString("link_1080p")     // Link to 1080p video
                );
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        }
        return null; // Return null if no video is found or an error occurs
    }

    // Add a new video to the database

    /**
     * To add a new video in the database
     */
    public boolean addVideo(Video video) {
        /** To add a new video to the database */
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
}