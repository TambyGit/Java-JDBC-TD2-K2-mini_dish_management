package com.minidishmanagement;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataRetriever {

    private final DBConnection dbConnection = new DBConnection();

    public Dish findDishById(Integer id) {
        Dish d = null;
        String sql = "SELECT id, name, dish_type, selling_price FROM dish WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    d = mapDishFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur findDishById : " + e.getMessage());
        }
        return d;
    }

    public List<Dish> findAllDishes() {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT id, name, dish_type, selling_price FROM dish ORDER BY id";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Dish dish = mapDishFromResultSet(rs);
                loadDishIngredients(dish);
                dishes.add(dish);
            }
        } catch (SQLException | IllegalArgumentException e) {
            System.err.println("Erreur findAllDishes : " + e.getMessage());
        }
        return dishes;
    }

    private Dish mapDishFromResultSet(ResultSet rs) throws SQLException {
        Dish dish = new Dish();
        dish.setId(rs.getInt("id"));
        dish.setName(rs.getString("name"));
        dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
        dish.setPrice(rs.getObject("selling_price") != null ? rs.getDouble("selling_price") : null);
        return dish;
    }

    private void loadDishIngredients(Dish dish) {
        List<DishIngredient> ingredients = findDishIngredientsByDishId(dish.getId());
        for (DishIngredient di : ingredients) {
            di.setDish(dish);
        }
        dish.setIngredients(ingredients);
    }

    public List<DishIngredient> findDishIngredientsByDishId(Integer dishId) {
        if (dishId == null) return new ArrayList<>();
        List<DishIngredient> ingredients = new ArrayList<>();
        String sql = """
                    SELECT di.id, di.quantity_required, di.unit,
                           i.id AS ingredient_id, i.name AS ingredient_name,
                           i.price AS ingredient_price, i.category AS ingredient_category
                    FROM dish_ingredient di
                    JOIN ingredient i ON di.id_ingredient = i.id
                    WHERE di.id_dish = ?
                """;
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ingredient ing = new Ingredient(rs.getInt("ingredient_id"), rs.getString("ingredient_name"),
                            CategoryEnum.valueOf(rs.getString("ingredient_category")), rs.getDouble("ingredient_price"));
                    DishIngredient di = new DishIngredient(rs.getInt("id"), ing, rs.getDouble("quantity_required"),
                            UnitTypeEnum.valueOf(rs.getString("unit")));
                    ingredients.add(di);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur ingrédients plat " + dishId + " : " + e.getMessage());
        }
        return ingredients;
    }

    // --- GESTION DES TABLES (SANS LA TABLE ORDER) ---

    public boolean isTableAvailable(Integer tableNumber, Instant dateTime) {
        if (tableNumber == null || dateTime == null) return false;

        String sql = """
                SELECT COUNT(*) as count
                FROM "table" t
                WHERE t.number = ?
                  AND NOT EXISTS (
                      SELECT 1
                      FROM table_order tord
                      WHERE tord.id_table = t.id
                        AND tord.arrival_datetime <= ?
                        AND (tord.departure_datetime IS NULL OR tord.departure_datetime > ?)
                  )
                """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tableNumber);
            ps.setTimestamp(2, Timestamp.from(dateTime));
            ps.setTimestamp(3, Timestamp.from(dateTime));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur vérification disponibilité table : " + e.getMessage());
        }
        return false;
    }

    public List<Integer> getAvailableTableNumbers(Instant dateTime) {
        List<Integer> availableTables = new ArrayList<>();
        if (dateTime == null) return availableTables;

        String sql = """
                SELECT t.number
                FROM "table" t
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM table_order tord
                    WHERE tord.id_table = t.id
                      AND tord.arrival_datetime <= ?
                      AND (tord.departure_datetime IS NULL OR tord.departure_datetime > ?)
                )
                ORDER BY t.number
                """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.from(dateTime));
            ps.setTimestamp(2, Timestamp.from(dateTime));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    availableTables.add(rs.getInt("number"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération tables disponibles : " + e.getMessage());
        }
        return availableTables;
    }

    public Table findTableByNumber(Integer number) {
        if (number == null) return null;
        String sql = "SELECT id, number FROM \"table\" WHERE number = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, number);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Table(rs.getInt("id"), rs.getInt("number"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur findTableByNumber : " + e.getMessage());
        }
        return null;
    }

    // --- GESTION DES STOCKS ---

    public StockValue getCurrentStockValue(Integer ingredientId) {
        if (ingredientId == null) return null;
        String sql = "SELECT initial_quantity, initial_unit, name FROM ingredient WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    double initialQty = rs.getDouble("initial_quantity");
                    UnitTypeEnum unit = UnitTypeEnum.valueOf(rs.getString("initial_unit"));

                    double totalKg = (unit == UnitTypeEnum.KG) ? initialQty : UnitConversion.convertToKg(name, initialQty, unit);

                    // Calcul des mouvements
                    List<StockMovement> movements = findStockMovementsByIngredientId(ingredientId);
                    for (StockMovement m : movements) {
                        double qtyKg = UnitConversion.convertToKg(name, m.getValue().getQuantity(), m.getValue().getUnit());
                        totalKg += (m.getType() == MovementTypeEnum.OUT) ? -qtyKg : qtyKg;
                    }
                    return new StockValue(totalKg, UnitTypeEnum.KG);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur stock : " + e.getMessage());
        }
        return null;
    }

    public List<StockMovement> findStockMovementsByIngredientId(Integer ingredientId) {
        List<StockMovement> movements = new ArrayList<>();
        String sql = "SELECT id, quantity, unit, movement_type, creation_datetime FROM stock_movement WHERE id_ingredient = ? ORDER BY creation_datetime ASC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockValue val = new StockValue(rs.getDouble("quantity"), UnitTypeEnum.valueOf(rs.getString("unit")));
                    StockMovement m = new StockMovement();
                    m.setId(rs.getInt("id"));
                    m.setValue(val);
                    m.setType(MovementTypeEnum.valueOf(rs.getString("movement_type")));
                    m.setCreationDatetime(rs.getTimestamp("creation_datetime").toInstant());
                    movements.add(m);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur mouvements : " + e.getMessage());
        }
        return movements;
    }

    private String getIngredientNameById(Integer ingredientId) {
        String sql = "SELECT name FROM ingredient WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("name");
            }
        } catch (SQLException e) { /**/ }
        return "Ingrédient #" + ingredientId;
    }
}