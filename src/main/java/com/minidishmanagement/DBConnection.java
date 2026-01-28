package com.minidishmanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class DBConnection {

    Dotenv dotenv = Dotenv.load();
    private final String URL = dotenv.get("DB_URL");
    private final String USER = dotenv.get("DB_USER");
    private final String PASSWORD = dotenv.get("DB_PASSWORD");

    public DBConnection() {
    }

    public Connection getConnection() throws SQLException {
        if (URL == null || USER == null || PASSWORD == null) {
            throw new IllegalArgumentException("Url, Username and password are mandatory");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
