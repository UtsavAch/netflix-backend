package com.dovydasvenckus.jersey;

import com.dovydasvenckus.jersey.models.User;
import com.dovydasvenckus.jersey.models.Video;
import com.dovydasvenckus.jersey.services.UserService;
import com.dovydasvenckus.jersey.services.VideoService;

import java.util.List;

public class MainOld {
    public static void main(String[] args) {
        ////////////////////////////////////////////////
        ////////////////////////////////////////////////
        /// Check for Video Queries ///
        VideoService videoService = new VideoService();

        /**
        //Get all the videos in the database
        List<Video> videos = videoService.getAllVideos();

        if (videos.isEmpty()) {
            System.out.println("No videos found in the database.");
        } else {
            System.out.println("Videos in the database:");
            for (Video video : videos) {
                System.out.println("ID: " + video.getId() + ", Name: " + video.getName() +
                        ", Genre: " + video.getGenre() + ", Duration: " + video.getDuration() +
                        " mins, Link (360p): " + video.getLink360p() +
                        ", Link (1080p): " + video.getLink1080p());
            }
        }
         */

        // Retrieve a video by its ID
        //UNCOMMENT TO USE IT

        /**
        Video video = videoService.getVideoById(2);

        // Check if the video exists and print its details
        if (video != null) {
            System.out.println("Video ID: " + video.getId());
            System.out.println("Name: " + video.getName());
            System.out.println("Genre: " + video.getGenre());
            System.out.println("Duration: " + video.getDuration() + " minutes");
            System.out.println("Link (360p): " + video.getLink360p());
            System.out.println("Link (1080p): " + video.getLink1080p());
        } else {
            System.out.println("Video not found!");
        }
       */

        //Add new user to the database
        //UNCOMMENT TO USE IT

        /**
        Video newVideo = new Video(
                0,
                "Tom and Jerry",
                "Animation",
                111,
                "link_to_360p.com",
                "link_to_1080p.com"
        );
        // Attempt to insert the video into the database
        boolean success = videoService.addVideo(newVideo);
        if (success) {
            System.out.println("Video added successfully!");
        } else {
            System.out.println("Failed to add video.");
        }
*/

        ////////////////////////////////////////////////
        ////////////////////////////////////////////////
        /// Check for User Queries ///
        UserService userService = new UserService();

        //Get all users from the database
        // UNCOMMENT TO USE IT
        /**
         List<User> users = userService.getAllUsers();

         for (User user : users) {
         System.out.println("ID: " + user.getId() + ", Name: " + user.getName() + ", Email: " + user.getEmail());
         }
        */

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

        // Add new user to the database

        User newUser = new User(
                0,
                "Jims Boes",
                "jims.boes@example.com",
                "jim123", "admin", 1);

        boolean isAdded = userService.addUser(newUser);
        if (isAdded) {
            System.out.println("New user added successfully!");
        } else {
            System.out.println("Failed to add new user.");
        }


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

