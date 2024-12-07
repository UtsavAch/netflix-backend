package com.dovydasvenckus.jersey;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        VideoService videoService = new VideoService();
        Video video = videoService.getVideoById(1);
        if (video != null) {
            System.out.println("Video ID: " + video.getId());
            System.out.println("Name: " + video.getName());
            System.out.println("File Path: " + video.getFilePath());
        } else {
            System.out.println("Video not found!");
        }

        ////////////////////////////////////////////////
        /// Check for User Queries ///
        UserService userService = new UserService();

        //Get user by ID
        // UNCOMMENT TO USE IT
        /**
        User user = userService.getUserById(3);
        if (user != null) {
            System.out.println("User ID: " + user.getId());
            System.out.println("Username: " + user.getName());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Email: " + user.getPassword());
            System.out.println("Email: " + user.getRole());
            System.out.println("Email: " + user.getCreatedAt());
        } else {
            System.out.println("User not found!");
        }
         */

        //Add new user to the database
        //UNCOMMENT TO USE IT
        /**
        User newUser = new User(
                0, // ID will be auto-generated
                "Joe Toe",
                "joe.toe@example.com",
                "joe123", // Password should be hashed in production later
                "user",
                LocalDateTime.now()
        );

        boolean isAdded = userService.addUser(newUser);
        if (isAdded) {
            System.out.println("New user added successfully!");
        } else {
            System.out.println("Failed to add new user.");
        }
         */

        //Delete the user from database by ID
        //This query is only used by the admin
        //The admin can't be deleted by this query
        //UNCOMMENT TO USE IT //
        /**
        int userIdToDelete = 3;
        boolean isDeleted = userService.deleteUserById(userIdToDelete);
        if (isDeleted) {
            System.out.println("User with ID " + userIdToDelete + " was successfully deleted.");
        } else {
            System.out.println("Failed to delete user with ID " + userIdToDelete + ".");
        }
         */


        // Delete a user account by email and password
        //UNCOMMENT TO USE It//
        /**
        String email = "joe.toe@example.com";
        String password = "joe123";
        boolean isDeleted = userService.deleteUserByEmailAndPassword(email, password);
        if (isDeleted) {
            System.out.println("User account deleted successfully!");
        } else {
            System.out.println("Failed to delete user account. Check credentials or role restrictions.");
        }
         */

        // Login to the CMS application as an admin. Other users cannot log in
        //UNCOMMENT TO USE It//
        /**
        boolean canLogin = userService.loginToCMS("alice.doe@example.com", "password123");

        if (canLogin) {
            System.out.println("Admin successfully logged in.");
        } else {
            System.out.println("Login failed.");
        }
         */

        // Login to the application(main application). Every valid users cannot log in
        //UNCOMMENT TO USE It//
        /**
        boolean canLogin = userService.login("alta.poe@example.com", "alta123");

        if (canLogin) {
            System.out.println("User successfully logged in.");
        } else {
            System.out.println("Login failed.");
        }
         */

    }
}
