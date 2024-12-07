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

    }
}
