package com.dovydasvenckus.jersey;

import java.sql.*;
import java.time.LocalDateTime;

public class UserService {

    public User getUserById(int id) {
        /**To get user from the database by his ID*/
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
        String findVacantIdQuery = "SELECT MIN(t1.id + 1) AS vacant_id " +
                "FROM users t1 " +
                "LEFT JOIN users t2 ON t1.id + 1 = t2.id " +
                "WHERE t2.id IS NULL";
        String insertUserQuery = "INSERT INTO users (id, name, email, password, role, created_at) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement findVacantStmt = conn.prepareStatement(findVacantIdQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertUserQuery)) {

            // Step 1: Find the first vacant ID
            int vacantId = -1; // Default value to indicate no vacant ID
            ResultSet rs = findVacantStmt.executeQuery();
            if (rs.next()) {
                vacantId = rs.getInt("vacant_id"); // Get the vacant ID
            }

            // Step 2: Insert the user
            insertStmt.setInt(1, vacantId > 0 ? vacantId : user.getId()); // Use vacant ID if found, else fallback to auto-increment
            insertStmt.setString(2, user.getName());
            insertStmt.setString(3, user.getEmail());
            insertStmt.setString(4, user.getPassword());
            insertStmt.setString(5, user.getRole());
            insertStmt.setTimestamp(6, Timestamp.valueOf(user.getCreatedAt()));

            // Execute the insertion
            int rowsInserted = insertStmt.executeUpdate();
            return rowsInserted > 0; // Return true if insertion was successful
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        }
        return false; // Return false if insertion failed
    }


    // Delete a user from the database by ID, ensuring admins cannot be deleted
    public boolean deleteUserById(int id) {
        /** To delete the user from the database by ID. This method cannot delete the admins from the database */
        String checkRoleQuery = "SELECT role FROM users WHERE id = ?";
        String deleteQuery = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkRoleQuery);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {

            // Check the role of the user
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                if ("admin".equalsIgnoreCase(role)) {
                    System.out.println("Admins cannot be deleted.");
                    return false;
                }
            } else {
                System.out.println("User with ID " + id + " not found.");
                return false;
            }

            // Proceed to delete the user
            deleteStmt.setInt(1, id);
            int rowsDeleted = deleteStmt.executeUpdate();
            return rowsDeleted > 0; // Return true if a user was deleted
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if deletion failed
    }
}
