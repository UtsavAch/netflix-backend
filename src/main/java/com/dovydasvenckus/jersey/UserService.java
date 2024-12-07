package com.dovydasvenckus.jersey;

import java.sql.*;
import java.time.LocalDateTime;

public class UserService {

    /**To get user from the database by his ID*/
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
                        rs.getString("password")             // Password
                );
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        }
        return null; // Return null if no user found or an error occurred
    }

    // Add a new user to the database
    public boolean addUser(User user) {
        /** To add a new user to the database */
        String findVacantIdQuery = "SELECT MIN(t1.id + 1) AS vacant_id " +
                "FROM users t1 " +
                "LEFT JOIN users t2 ON t1.id + 1 = t2.id " +
                "WHERE t2.id IS NULL";
        String insertUserQuery = "INSERT INTO users (id, name, email, password) VALUES (?, ?, ?, ?)";

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
            int idToUse = (vacantId > 0) ? vacantId : user.getId();
            insertStmt.setInt(1, idToUse);
            insertStmt.setString(2, user.getName());
            insertStmt.setString(3, user.getEmail());
            insertStmt.setString(4, user.getPassword());

            // Execute the insertion
            int rowsInserted = insertStmt.executeUpdate();
            return rowsInserted > 0; // Return true if insertion was successful
        } catch (SQLException e) {
            // Log the exception
            e.printStackTrace();
        }
        return false; // Return false if insertion failed
    }



    // Delete a user from the database by ID, ensuring admins cannot be deleted
    /** To delete the user from the database by ID. This method cannot delete the admins
     * from the database. And, only admins can use this query */
    public boolean deleteUserById(int id) {
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

    // Delete a user from the database by email and password
    /** To delete a user from the database using email and password.
     * This method ensures that the user can only delete their own account.
     * Admins cannot delete their accounts using this method.
     */
    public boolean deleteUserByEmailAndPassword(String email, String password) {
        String checkUserQuery = "SELECT role FROM users WHERE email = ? AND password = ?";
        String deleteQuery = "DELETE FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkUserQuery);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {

            // Check if the user exists and is not an admin
            checkStmt.setString(1, email);
            checkStmt.setString(2, password);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                if ("admin".equalsIgnoreCase(role)) {
                    System.out.println("Admins cannot delete their accounts using this method.");
                    return false;
                }
            } else {
                System.out.println("Invalid email or password.");
                return false;
            }

            // Proceed to delete the user
            deleteStmt.setString(1, email);
            deleteStmt.setString(2, password);
            int rowsDeleted = deleteStmt.executeUpdate();
            return rowsDeleted > 0; // Return true if a user was deleted
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        }
        return false; // Return false if deletion failed
    }

    /**
     * Helper method to set login_status to 1 for the user
     */
    private boolean updateLoginStatus(String email) {
        String updateQuery = "UPDATE users SET login_status = 1 WHERE email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, email);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if a row was updated
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Implement CMS login, where an user can login into the CMS application
    // The thing is that the user must be admin. Only admins can login to the CMS application
    /** To allow a user to login to the CMS application.
     * Only admins are allowed to log in.
     */
    public boolean loginToCMS(String email, String password) {
        String checkAdminQuery = "SELECT role FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkAdminQuery)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                if ("admin".equalsIgnoreCase(role)) {
                    System.out.println("Login successful. Welcome to the CMS.");

                    // Update login status after successful login
                    if (updateLoginStatus(email)) {
                        return true;
                    } else {
                        System.out.println("Failed to set login status.");
                    }
                } else {
                    System.out.println("Access denied. Only admins can log in to the CMS.");
                }
            } else {
                System.out.println("Invalid email or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Implement login for the main application
    /** To allow a user to login to the main application.
     * Any valid user in the database can log in.
     */
    public boolean login(String email, String password) {
        String checkUserQuery = "SELECT id, name FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkUserQuery)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                String userName = rs.getString("name");
                System.out.println("Login successful. Welcome, " + userName + " (User ID: " + userId + ").");

                // Update login status after successful login
                if (updateLoginStatus(email)) {
                    return true;
                } else {
                    System.out.println("Failed to set login status.");
                }
            } else {
                System.out.println("Invalid email or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
