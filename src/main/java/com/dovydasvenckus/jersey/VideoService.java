package com.dovydasvenckus.jersey;

import java.sql.*;
import java.time.LocalDateTime;

public class VideoService {

    //Get video from the database by its ID
    /**To get video from the database by its ID*/
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
                        rs.getString("link_1080p"),     // Link to 1080p video
                        rs.getTimestamp("created_at")   // Created at timestamp
                                .toLocalDateTime()     // Convert to LocalDateTime
                );
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        }
        return null; // Return null if no video is found or an error occurs
    }

    // Add a new video to the database
    /**To add a new video in the database*/
    public boolean addVideo(Video video) {
        String insertQuery = "INSERT INTO movies (name, genre, duration, link_360p, link_1080p, created_at) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setString(1, video.getName());
            stmt.setString(2, video.getGenre());
            stmt.setInt(3, video.getDuration());
            stmt.setString(4, video.getLink360p());
            stmt.setString(5, video.getLink1080p());
            stmt.setTimestamp(6, Timestamp.valueOf(video.getCreatedAt()));

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0; // Return true if the video was added successfully
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Return false if insertion failed
    }

}
