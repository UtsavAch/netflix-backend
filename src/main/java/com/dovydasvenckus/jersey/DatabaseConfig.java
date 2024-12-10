package com.dovydasvenckus.jersey;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mariadb://34.147.77.68:3306/netflix_db"; // MariaDB URL
        //String url = "jdbc:mariadb://localhost:3306/netflix_db"; // MariaDB URL in used in vm
        String user = "root"; //Default username is root maybe
        String password = "S@cure1Pass"; //Default password is root maybe
        return DriverManager.getConnection(url, user, password);
    }
}
