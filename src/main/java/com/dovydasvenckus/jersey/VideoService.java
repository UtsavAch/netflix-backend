package com.dovydasvenckus.jersey;

import java.sql.*;

public class VideoService {

    public Video getVideoById(int id) {
        String query = "SELECT * FROM movies WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();  // Get connection from DatabaseConfig
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Video(rs.getInt("id"), rs.getString("name"), rs.getString("link_360p"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
