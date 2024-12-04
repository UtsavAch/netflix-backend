package com.dovydasvenckus.jersey;

public class Main {
    public static void main(String[] args) {
        VideoService videoService = new VideoService();
        Video video = videoService.getVideoById(1);
        if (video != null) {
            System.out.println("Video ID: " + video.getId());
            System.out.println("Title: " + video.getTitle());
            System.out.println("File Path: " + video.getFilePath());
        } else {
            System.out.println("Video not found!");
        }

        UserService userService = new UserService();
        User user = userService.getUserById(1);
        if (user != null) {
            System.out.println("User ID: " + user.getId());
            System.out.println("Username: " + user.getUsername());
            System.out.println("Email: " + user.getEmail());
        } else {
            System.out.println("User not found!");
        }
    }
}
