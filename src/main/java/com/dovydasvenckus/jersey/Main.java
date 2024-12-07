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
        User user = userService.getUserById(1);
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
        User newUser = new User(
                0, // ID will be auto-generated
                "Alice Doe",
                "alice.doe@example.com",
                "password123", // Password should be hashed in production later
                "admin",
                LocalDateTime.now()
        );

        boolean isAdded = userService.addUser(newUser);
        if (isAdded) {
            System.out.println("New user added successfully!");
        } else {
            System.out.println("Failed to add new user.");
        }

    }
}
