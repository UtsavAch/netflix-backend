package com.dovydasvenckus.jersey;

import java.sql.*;
import java.time.LocalDateTime;

public class UserService {

    public User getUserById(int id) {
        String query = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the parameter for the query
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            // Process the result set
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),                      // ID
                        rs.getString("name"),                 // Name
                        rs.getString("email"),                // Email
                        rs.getString("password"),             // Password
                        rs.getString("role"),                 // Role (can be null)
                        rs.getTimestamp("created_at")          // Convert to LocalDateTime
                                .toLocalDateTime()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        }
        return null; // Return null if no user found or an error occurred
    }

    // Add a new user to the database
    public boolean addUser(User user) {
        String query = "INSERT INTO users (name, email, password, role, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set parameters for the query
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());
            stmt.setTimestamp(5, Timestamp.valueOf(user.getCreatedAt()));

            // Execute the query
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0; // Return true if at least one row was inserted
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        }
        return false; // Return false if insertion failed
    }
}
