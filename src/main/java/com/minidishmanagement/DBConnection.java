package com.minidishmanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private final String url = "jdbc:postgresql://localhost:5432/mini_dish_db";
    private final String user = "mini_dish_db_manager";
    private final String password = "123456";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
