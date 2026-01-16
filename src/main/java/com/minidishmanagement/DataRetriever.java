package com.minidishmanagement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private final DBConnection dbConnection = new DBConnection();

    public Dish findDishById(Integer id) {
        if (id == null || id <= 0) return null;

        String sql = "SELECT id, name, dish_type, price FROM dish WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int dishId = rs.getInt("id");
                String name = rs.getString("name");
                DishTypeEnum type = DishTypeEnum.valueOf(rs.getString("dish_type"));
                Double price = rs.getObject("price") != null ? rs.getDouble("price") : null;

                List<Ingredient> ingredients = loadIngredientsForDish(dishId, conn);
                return new Dish(dishId, name, type, price, ingredients);
            }
        } catch (SQLException e) {
            System.err.println("Erreur findDishById : " + e.getMessage());
        }
        return null;
    }

    private List<Ingredient> loadIngredientsForDish(int dishId, Connection conn) throws SQLException {
        String sql = "SELECT id, name, price, category FROM ingredient WHERE id_dish = ?";
        List<Ingredient> ingredients = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dishId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int ingId = rs.getInt("id");
                String ingName = rs.getString("name");
                double price = rs.getDouble("price");
                CategorieEnum category = CategorieEnum.valueOf(rs.getString("category"));
                ingredients.add(new Ingredient(ingId, ingName, price, category, null));
            }
        }
        return ingredients;
    }

    public List<Ingredient> findIngredients(int page, int size) {
        return findIngredientsByCriteria(null, null, null, page, size);
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        if (newIngredients == null || newIngredients.isEmpty()) return new ArrayList<>();

        String checkSql = "SELECT COUNT(*) FROM ingredient WHERE name = ?";
        String insertSql = "INSERT INTO ingredient (name, price, category, id_dish) VALUES (?, ?, ?::ingredient_category_enum, ?) RETURNING id";

        List<Ingredient> created = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);

            for (Ingredient ing : newIngredients) {
                try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                    check.setString(1, ing.getName());
                    ResultSet rs = check.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        conn.rollback();
                        throw new RuntimeException("Ingrédient déjà existant : " + ing.getName());
                    }
                }

                try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
                    insert.setString(1, ing.getName());
                    insert.setDouble(2, ing.getPrice());
                    insert.setString(3, ing.getCategory().name());
                    insert.setObject(4, ing.getDish() != null ? ing.getDish().getId() : null);

                    ResultSet rs = insert.executeQuery();
                    if (rs.next()) {
                        int newId = rs.getInt(1);
                        created.add(new Ingredient(newId, ing.getName(), ing.getPrice(), ing.getCategory(), ing.getDish()));
                    }
                }
            }
            conn.commit();
        } catch (SQLException | RuntimeException e) {
            System.err.println("Erreur création ingrédients : " + e.getMessage());
        }
        return created;
    }

    public Dish saveDish(Dish dishToSave) {
        if (dishToSave == null) return null;

        String checkSql = "SELECT COUNT(*) FROM dish WHERE id = ?";
        String insertDishSql = "INSERT INTO dish (name, dish_type, price) VALUES (?, ?::dish_type_enum, ?) RETURNING id";
        String updateDishSql = "UPDATE dish SET name = ?, dish_type = ?::dish_type_enum, price = ? WHERE id = ?";
        String deleteIngredientsSql = "DELETE FROM ingredient WHERE id_dish = ?";
        String insertIngredientSql = "INSERT INTO ingredient (name, price, category, id_dish) VALUES (?, ?, ?::ingredient_category_enum, ?) RETURNING id";
        String updateIngredientLinkSql = "UPDATE ingredient SET id_dish = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);

            int dishId = dishToSave.getId();

            if (dishId <= 0) {
                try (PreparedStatement stmt = conn.prepareStatement(insertDishSql)) {
                    stmt.setString(1, dishToSave.getName());
                    stmt.setString(2, dishToSave.getDishTypeEnum().name());
                    stmt.setObject(3, dishToSave.getPrice());
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        dishId = rs.getInt(1);
                    }
                }
            } else {
                try (PreparedStatement stmt = conn.prepareStatement(updateDishSql)) {
                    stmt.setString(1, dishToSave.getName());
                    stmt.setString(2, dishToSave.getDishTypeEnum().name());
                    stmt.setObject(3, dishToSave.getPrice());
                    stmt.setInt(4, dishId);
                    stmt.executeUpdate();
                }

                try (PreparedStatement delete = conn.prepareStatement(deleteIngredientsSql)) {
                    delete.setInt(1, dishId);
                    delete.executeUpdate();
                }
            }

            List<Ingredient> savedIngredients = new ArrayList<>();
            if (dishToSave.getIngredients() != null) {
                for (Ingredient ing : dishToSave.getIngredients()) {
                    if (ing.getId() > 0) {
                        try (PreparedStatement stmt = conn.prepareStatement(updateIngredientLinkSql)) {
                            stmt.setInt(1, dishId);
                            stmt.setInt(2, ing.getId());
                            stmt.executeUpdate();
                            savedIngredients.add(ing);
                        }
                    } else {
                        try (PreparedStatement stmt = conn.prepareStatement(insertIngredientSql)) {
                            stmt.setString(1, ing.getName());
                            stmt.setDouble(2, ing.getPrice());
                            stmt.setString(3, ing.getCategory().name());
                            stmt.setInt(4, dishId);
                            ResultSet rs = stmt.executeQuery();
                            if (rs.next()) {
                                int newId = rs.getInt(1);
                                savedIngredients.add(new Ingredient(newId, ing.getName(), ing.getPrice(), ing.getCategory(), null));
                            }
                        }
                    }
                }
            }

            conn.commit();
            return new Dish(dishId, dishToSave.getName(), dishToSave.getDishTypeEnum(), dishToSave.getPrice(), savedIngredients);

        } catch (SQLException e) {
            System.err.println("Erreur saveDish : " + e.getMessage());
            return dishToSave;
        }
    }

    public List<Dish> findDishesByIngredientName(String ingredientName) {
        if (ingredientName == null || ingredientName.trim().isEmpty()) return new ArrayList<>();

        String sql = """
                SELECT DISTINCT d.id, d.name, d.dish_type, d.price
                FROM dish d
                JOIN ingredient i ON d.id = i.id_dish
                WHERE i.name ILIKE ?
                """;

        List<Dish> dishes = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + ingredientName.trim() + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int dishId = rs.getInt("id");
                String name = rs.getString("name");
                DishTypeEnum type = DishTypeEnum.valueOf(rs.getString("dish_type"));
                Double price = rs.getObject("price") != null ? rs.getDouble("price") : null;

                List<Ingredient> ingredients = loadIngredientsForDish(dishId, conn);
                dishes.add(new Dish(dishId, name, type, price, ingredients));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findDishesByIngredientName : " + e.getMessage());
        }
        return dishes;
    }

    public List<Ingredient> findIngredientsByCriteria(String ingredientName,
                                                      CategorieEnum category,
                                                      String dishName,
                                                      int page,
                                                      int size) {
        StringBuilder sql = new StringBuilder(
                "SELECT i.id, i.name, i.price, i.category, i.id_dish, " +
                        "d.name AS dish_name, d.dish_type, d.price AS dish_price " +
                        "FROM ingredient i " +
                        "LEFT JOIN dish d ON i.id_dish = d.id " +
                        "WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (ingredientName != null && !ingredientName.trim().isEmpty()) {
            sql.append(" AND i.name ILIKE ?");
            params.add("%" + ingredientName.trim() + "%");
        }
        if (category != null) {
            sql.append(" AND i.category = ?::ingredient_category_enum");
            params.add(category.name());
        }
        if (dishName != null && !dishName.trim().isEmpty()) {
            sql.append(" AND d.name ILIKE ?");
            params.add("%" + dishName.trim() + "%");
        }

        sql.append(" ORDER BY i.id LIMIT ? OFFSET ?");
        params.add(size);
        params.add((page - 1) * size);

        List<Ingredient> ingredients = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                CategorieEnum cat = CategorieEnum.valueOf(rs.getString("category"));

                Integer dishIdObj = (Integer) rs.getObject("id_dish");
                Dish associatedDish = null;
                if (dishIdObj != null && dishIdObj > 0) {
                    String dName = rs.getString("dish_name");
                    String dishTypeStr = rs.getString("dish_type");
                    Double dishPrice = rs.getObject("dish_price") != null ? rs.getDouble("dish_price") : null;
                    if (dName != null && dishTypeStr != null) {
                        DishTypeEnum dishType = DishTypeEnum.valueOf(dishTypeStr);
                        associatedDish = new Dish(dishIdObj, dName, dishType, dishPrice, null);
                    }
                }

                ingredients.add(new Ingredient(id, name, price, cat, associatedDish));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findIngredientsByCriteria : " + e.getMessage());
        }
        return ingredients;
    }
}