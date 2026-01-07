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
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<Ingredient> findIngredients(int page, int size) {
        String sql = "SELECT id, name, price, category, id_dish FROM ingredient ORDER BY id LIMIT ? OFFSET ?";
        List<Ingredient> ingredients = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, size);
            stmt.setInt(2, (page - 1) * size);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                CategorieEnum category = CategorieEnum.valueOf(rs.getString("category"));
                int dishId = rs.getInt("id_dish");
                Dish dish = findDishById(dishId);
                ingredients.add(new Ingredient(id, name, price, category, dish));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ingredients;
    }


}