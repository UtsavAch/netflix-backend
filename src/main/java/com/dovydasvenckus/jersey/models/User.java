package com.dovydasvenckus.jersey.models;

import java.time.LocalDateTime;

public class User {
    private int id;                // Primary key, auto-incremented
    private String name;           // Not null
    private String email;          // Not null, unique
    private String password;       // Not null
    private String role;           // Can be null, default is "user"
    private int login_status; // When the user is logged in

    // constructor for json deserialization
    public User() {}

    // Constructor
    public User(int id, String name, String email, String password, String role, int login_status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.login_status = login_status;
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

    public String getRole(){return role;}

    public int getLoginStatus() {
        return login_status;
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
        this.role = role;

    }
    public void setLoginStatus(int login_status) {
        this.login_status = login_status;
    }

    @Override
    public String toString() {
        return "User{id=" + id +
                ", name='" + name +
                "', email='" + email +
                "', password='" + password +
                "', role='" + role +
                "', loginStatus=" + login_status +
                "}";
    }
}
