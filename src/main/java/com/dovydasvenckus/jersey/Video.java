package com.dovydasvenckus.jersey;

public class Video {
    private int id;
    private String title;
    private String filePath;

    public Video(int id, String title, String filePath) {
        this.id = id;
        this.title = title;
        this.filePath = filePath;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getFilePath() {
        return filePath;
    }
}
