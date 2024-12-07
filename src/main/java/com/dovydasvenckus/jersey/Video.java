package com.dovydasvenckus.jersey;

public class Video {
    private int id;
    private String name;
    private String filePath;

    public Video(int id, String name, String filePath) {
        this.id = id;
        this.name = name;
        this.filePath = filePath;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFilePath() {
        return filePath;
    }
}
