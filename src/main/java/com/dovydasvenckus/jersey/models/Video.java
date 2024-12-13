package com.dovydasvenckus.jersey.models;

import java.time.LocalDateTime;

public class Video {
    private int id;
    private String name;
    private String genre;
    private int duration; // Duration in minutes
    private String link360p; // URL for 360p quality video
    private String link1080p; // URL for 1080p quality video

    // Constructor for initializing all fields
    public Video(int id, String name, String genre, int duration, String link360p, String link1080p) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.duration = duration;
        this.link360p = link360p;
        this.link1080p = link1080p;
    }

    // Constructor for creating a video without an ID (e.g., when adding a new video to the database)
    public Video(String name, String genre, int duration, String link360p, String link1080p) {
        this.name = name;
        this.genre = genre;
        this.duration = duration;
        this.link360p = link360p;
        this.link1080p = link1080p;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getLink360p() {
        return link360p;
    }

    public void setLink360p(String link360p) {
        this.link360p = link360p;
    }

    public String getLink1080p() {
        return link1080p;
    }

    public void setLink1080p(String link1080p) {
        this.link1080p = link1080p;
    }
}
