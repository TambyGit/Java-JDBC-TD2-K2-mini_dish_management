package com.minidishmanagement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private final DBConnection dbConnection = new DBConnection();

    public Dish findDishById(Integer id) {
        String sql = "SELECT id, name, dish_type FROM dish WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int dishId = rs.getInt("id");
                String name = rs.getString("name");
                DishTypeEnum type = DishTypeEnum.valueOf(rs.getString("dish_type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}