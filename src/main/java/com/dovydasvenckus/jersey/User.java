package com.dovydasvenckus.jersey;

import java.time.LocalDateTime;

public class User {
    private int id;                // Primary key, auto-incremented
    private String name;           // Not null
    private String email;          // Not null, unique
    private String password;       // Not null
    private String role;           // Can be null, default is "user"
    private LocalDateTime created_at; // When the user was created (current timestamp)

    // Constructor
    public User(int id, String name, String email, String password, String role, LocalDateTime created_at) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role != null ? role : "user"; // Default to "user" if null
        this.created_at = created_at;
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return created_at;
    }

    // Setter methods
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role != null ? role : "user"; // Default to "user" if null
    }

    public void setCreatedAt(LocalDateTime created_at) {
        this.created_at = created_at;
    }
}
