package com.dovydasvenckus.jersey.services;

import com.dovydasvenckus.jersey.DatabaseConfig;
import com.dovydasvenckus.jersey.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    /**To get  all users from the database*/
    public List<User> getAllUsers() {
        String query = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            // Process the result set
            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),           // ID
                        rs.getString("name"),      // Name
                        rs.getString("email"),     // Email
                        rs.getString("password"),   // Password
                        rs.getString("role"),       // Role
                        rs.getInt("login_status")   // Login status
                );
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        }
        return null;
    }

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
                        rs.getString("password"),             // Password
                        rs.getString("role"),       // Role
                        rs.getInt("login_status")   // Login status
                );
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        }
        return null; // Return null if no user found or an error occurred
    }

    // Add a new user to the database
    /** To add the new user to the database */
    public boolean addUser(User user) {
        // Query to find the first vacant ID
        String findVacantIdQuery = "SELECT MIN(t1.id + 1) AS vacant_id " +
                "FROM users t1 " +
                "LEFT JOIN users t2 ON t1.id + 1 = t2.id " +
                "WHERE t2.id IS NULL";

        // Insert query
        String insertUserQuery = "INSERT INTO users (id, name, email, password, role, login_status) VALUES (?, ?, ?, ?, ?, ?)";

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
            insertStmt.setString(5, user.getRole());
            insertStmt.setInt(6, user.getLoginStatus());

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
    public boolean deleteUserById(int adminId, int id) {
        String loginStatusQuery = "SELECT role, login_status FROM users WHERE id = ?";
        String checkRoleQuery = "SELECT role FROM users WHERE id = ?";
        String deleteQuery = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement loginStatusStmt = conn.prepareStatement(loginStatusQuery);
             PreparedStatement checkStmt = conn.prepareStatement(checkRoleQuery);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {

            // Check if adminId corresponds to a logged-in admin
            loginStatusStmt.setInt(1, adminId);
            ResultSet adminRs = loginStatusStmt.executeQuery();
            if (adminRs.next()) {
                String role = adminRs.getString("role");
                boolean loginStatus = adminRs.getBoolean("login_status");

                if (!"admin".equalsIgnoreCase(role) || !loginStatus) {
                    System.out.println("You must be a logged-in admin to delete a user.");
                    return false;
                }
            } else {
                System.out.println("Admin with ID " + adminId + " not found.");
                return false;
            }

            // Check the role of the user to be deleted
            checkStmt.setInt(1, id);
            ResultSet userRs = checkStmt.executeQuery();
            if (userRs.next()) {
                String userRole = userRs.getString("role");
                if ("admin".equalsIgnoreCase(userRole)) {
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
            if (rowsDeleted > 0) {
                System.out.println("User with ID " + id + " deleted successfully.");
                return true;
            } else {
                System.out.println("Failed to delete user with ID " + id + ".");
                return false;
            }

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
        String checkUserQuery = "SELECT role, login_status FROM users WHERE email = ? AND password = ?";
        String deleteQuery = "DELETE FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkUserQuery);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {

            // Check if the user exists and is logged in
            checkStmt.setString(1, email);
            checkStmt.setString(2, password);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                int loginStatus = rs.getInt("login_status");

                if (loginStatus != 1) {
                    System.out.println("You must be logged in to delete your account.");
                    return false;
                }

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
    public User loginToCMS(String email, String password) {
        String checkAdminQuery = "SELECT * FROM users WHERE email = ? AND password = ? AND role = 'admin'";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkAdminQuery)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User admin = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getInt("login_status")
                );

                // Update login status after successful login
                if (updateLoginStatus(email)) {
                    System.out.println("Login successful. Welcome to the CMS.");
                    return admin;
                } else {
                    System.out.println("Failed to set login status.");
                }
            } else {
                System.out.println("Invalid email, password, or access denied. Only admins can log in to the CMS.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if login fails
    }

    // Implement login for the main application
    /** To allow a user to login to the main application.
     * Any valid user in the database can log in.
     */
    public User login(String email, String password) {
        String checkUserQuery = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkUserQuery)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getInt("login_status")
                );

                // Update login status after successful login
                if (updateLoginStatus(email)) {
                    System.out.println("Login successful. Welcome, " + user.getName() + " (User ID: " + user.getId() + ").");
                    return user;
                } else {
                    System.out.println("Failed to set login status.");
                }
            } else {
                System.out.println("Invalid email or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if login fails
    }

    /**
     * Updates the password of the logged-in user.
     * A user can only change their own password.
     *
     * @param email       The email of the user requesting the password change.
     * @param oldPassword The current password of the user.
     * @param newPassword The new password to be set.
     * @return true if the password was successfully updated, false otherwise.
     */
    public boolean updatePassword(String email, String oldPassword, String newPassword) {
        try (Connection connection = DatabaseConfig.getConnection()) {
            // Verify the user's credentials and check login status
            String verifyQuery = "SELECT login_status FROM users WHERE email = ? AND password = ?";
            try (PreparedStatement verifyStmt = connection.prepareStatement(verifyQuery)) {
                verifyStmt.setString(1, email);
                verifyStmt.setString(2, oldPassword);
                ResultSet resultSet = verifyStmt.executeQuery();
                if (!resultSet.next()) {
                    return false; // Invalid email or password
                }

                int loginStatus = resultSet.getInt("login_status");
                if (loginStatus != 1) {
                    System.out.println("You must be logged in to change your password.");
                    return false;
                }
            }

            // Update the password
            String updateQuery = "UPDATE users SET password = ? WHERE email = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                updateStmt.setString(1, newPassword);
                updateStmt.setString(2, email);
                int rowsUpdated = updateStmt.executeUpdate();
                return rowsUpdated > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * Logs out a user by updating their login_status to 0.
     * The user can only log out if they are currently logged in (login_status = 1).
     *
     * @param email The email of the user who wants to log out.
     * @return true if the logout was successful, false otherwise.
     */
    public boolean logout(String email) {
        String checkStatusQuery = "SELECT login_status FROM users WHERE email = ?";
        String updateQuery = "UPDATE users SET login_status = 0 WHERE email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkStatusQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            // Step 1: Check the current login status
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int loginStatus = rs.getInt("login_status");
                if (loginStatus == 1) {
                    // User is logged in, proceed with logout
                    updateStmt.setString(1, email);
                    int rowsAffected = updateStmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Logout successful for user with email: " + email);
                        return true;
                    } else {
                        System.out.println("Failed to update login status for user with email: " + email);
                    }
                } else {
                    // User is not logged in
                    System.out.println("Logout failed: User with email " + email + " is not logged in.");
                }
            } else {
                // No user found with the given email
                System.out.println("Logout failed: No user found with email " + email + ".");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        }

        return false; // Return false if logout failed
    }

}
