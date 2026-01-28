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
        String sql = "select id, name ,dish_type, selling_price from dish where id = ?" ;
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int dishId = rs.getInt("id");
                String name = rs.getString("name");
                DishTypeEnum dishType = DishTypeEnum.valueOf(rs.getString("dish_type"));
                Double price = rs.getDouble("selling_price");
                d = new Dish(dishId, name, dishType, price, null);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return d;
    }

    public List<Dish> findAllDishes() {
        List<Dish> dishes = new ArrayList<>();
        String sql = """
                SELECT id, name, dish_type, selling_price
                FROM dish 
                ORDER BY id
                """;
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Dish dish = mapDishFromResultSet(rs);
                loadDishIngredients(dish);
                dishes.add(dish);
            }
        } catch (SQLException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
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
                    SELECT 
                        di.id, 
                        di.quantity_required, 
                        di.unit,
                        i.id AS ingredient_id,
                        i.name AS ingredient_name,
                        i.price AS ingredient_price,
                        i.category AS ingredient_category
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
        } catch (SQLException | IllegalArgumentException e) {
            System.err.println("Erreur lors du chargement des ingrédients du plat " + dishId + " : " + e.getMessage());
        }
        return ingredients;
    }

    public void saveDish(Dish dish) {
        if (dish == null) return;

        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);

            String sql = """
                INSERT INTO dish (name, dish_type, selling_price)
                VALUES (?, ?::dish_type_enum, ?)
                ON CONFLICT (name)
                DO UPDATE SET
                    dish_type = EXCLUDED.dish_type::dish_type_enum,
                    selling_price = EXCLUDED.selling_price
                RETURNING id
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, dish.getName());

                if (dish.getDishType() != null) {
                    ps.setString(2, dish.getDishType().name());
                } else {
                    ps.setNull(2, Types.VARCHAR);
                }

                if (dish.getPrice() != null) {
                    ps.setDouble(3, dish.getPrice());
                } else {
                    ps.setNull(3, Types.DOUBLE);
                }
            }

            // Sauvegarde des ingrédients du plat (sans toucher au stock ici)
            saveDishIngredientsInTransaction(dish, conn);

            conn.commit();

            System.out.println("Plat sauvegardé avec succès → id = " + dish.getId());

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction annulée (rollback effectué)");
                } catch (SQLException ex) {
                    System.err.println("Erreur rollback : " + ex.getMessage());
                }
            }
            System.err.println("Erreur lors de la sauvegarde du plat : " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Erreur fermeture connexion : " + e.getMessage());
                }
            }
        }
    }

    private void saveDishIngredientsInTransaction(Dish dish, Connection conn) throws SQLException {
        if (dish.getId() == null || dish.getIngredients() == null || dish.getIngredients().isEmpty()) {
            return;
        }

        String deleteSql = "DELETE FROM dish_ingredient WHERE id_dish = ?";
        try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
            ps.setInt(1, dish.getId());
            ps.executeUpdate();
        }

        String insertSql = """
                INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            for (DishIngredient di : dish.getIngredients()) {
                if (di.getIngredient() == null || di.getIngredient().getId() == null) {
                    continue;
                }
                ps.setInt(1, dish.getId());
                ps.setInt(2, di.getIngredient().getId());
                ps.setDouble(3, di.getQuantityRequired() != null ? di.getQuantityRequired() : 0.0);
                ps.setString(4, di.getUnit() != null ? di.getUnit().name() : null);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public void saveIngredient(Ingredient toSave) {
        if (toSave == null) return;

        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);

            String ingredientSql;
            boolean isNewIngredient = (toSave.getId() == null || toSave.getId() == 0);

            if (isNewIngredient) {
                ingredientSql = """
                        INSERT INTO ingredient (name, price, category, initial_quantity, initial_unit)
                        VALUES (?, ?, ?, ?, ?)
                        RETURNING id
                        """;
            } else {
                ingredientSql = """
                        UPDATE ingredient 
                        SET name = ?, price = ?, category = ?, 
                            initial_quantity = ?, initial_unit = ?
                        WHERE id = ?
                        """;
            }

            try (PreparedStatement ps = conn.prepareStatement(ingredientSql)) {
                ps.setString(1, toSave.getName());
                ps.setDouble(2, toSave.getPrice() != null ? toSave.getPrice() : 0.0);
                if (toSave.getCategory() != null) {
                    ps.setObject(3, toSave.getCategory().name(), Types.OTHER);
                } else {
                    ps.setNull(3, Types.OTHER);
                }

                StockValue initStock = toSave.getInitialStock();
                if (initStock != null) {
                    ps.setDouble(4, initStock.getQuantity());
                    ps.setObject(5, initStock.getUnit().name(), Types.OTHER);
                } else {
                    ps.setNull(4, Types.DOUBLE);
                    ps.setNull(5, Types.VARCHAR);
                }

                if (isNewIngredient) {
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            toSave.setId(rs.getInt("id"));
                        }
                    }
                } else {
                    ps.setInt(6, toSave.getId());
                    ps.executeUpdate();
                }
            }

            if (toSave.getStockMovementList() != null && !toSave.getStockMovementList().isEmpty()) {
                String movementSql = """
                        INSERT INTO stock_movement 
                            (id_ingredient, quantity, unit, movement_type, creation_datetime)
                        VALUES (?, ?, ?, ?, ?)
                        """;

                try (PreparedStatement ps = conn.prepareStatement(movementSql)) {
                    for (StockMovement movement : toSave.getStockMovementList()) {
                        ps.setInt(1, toSave.getId());
                        ps.setDouble(2, movement.getValue().getQuantity());
                        ps.setString(3, movement.getValue().getUnit().name());
                        ps.setString(4, movement.getType().name());
                        ps.setObject(5, movement.getCreationDatetime(), Types.TIMESTAMP);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }
            System.err.println("Erreur lors de la sauvegarde de l'ingrédient : " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public List<StockMovement> findStockMovementsByIngredientId(Integer ingredientId) {
        List<StockMovement> movements = new ArrayList<>();

        if (ingredientId == null) return movements;

        String sql = """
        SELECT id, quantity, unit, movement_type, creation_datetime
        FROM stock_movement
        WHERE id_ingredient = ?
        ORDER BY creation_datetime ASC
        """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ingredientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockValue val = new StockValue(
                            rs.getDouble("quantity"),
                            UnitTypeEnum.valueOf(rs.getString("unit"))
                    );
                    MovementTypeEnum type = MovementTypeEnum.valueOf(rs.getString("movement_type"));
                    Instant dt = rs.getTimestamp("creation_datetime").toInstant();

                    StockMovement m = new StockMovement();
                    m.setId(rs.getInt("id"));
                    m.setValue(val);
                    m.setType(type);
                    m.setCreationDatetime(dt);
                    movements.add(m);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lecture mouvements ingrédient " + ingredientId + " : " + e.getMessage());
        }
        return movements;
    }

    public Order saveOrder(Order orderToSave) {
        if (orderToSave == null || orderToSave.getDishOrders() == null || orderToSave.getDishOrders().isEmpty()) {
            throw new IllegalArgumentException("La commande doit contenir au moins un plat");
        }
        Map<Integer, Double> requiredIngredients = new HashMap<>();

        for (DishOrder dishOrder : orderToSave.getDishOrders()) {
            int quantity = dishOrder.getQuantity() != null ? dishOrder.getQuantity() : 0;
            if (quantity <= 0) continue;

            Dish dish = dishOrder.getDish();
            if (dish == null || dish.getIngredients() == null) continue;

            for (DishIngredient dishIngredient : dish.getIngredients()) {
                Ingredient ingredient = dishIngredient.getIngredient();
                if (ingredient == null || ingredient.getId() == null) continue;

                double quantityRequired = dishIngredient.getQuantityRequired() * quantity;
                requiredIngredients.merge(ingredient.getId(), quantityRequired, Double::sum);
            }
        }


        for (Map.Entry<Integer, Double> entry : requiredIngredients.entrySet()) {
            Integer ingredientId = entry.getKey();
            Double requiredQty = entry.getValue();

            StockValue currentStock = getCurrentStockValue(ingredientId);
            if (currentStock == null || currentStock.getQuantity() < requiredQty) {

                String ingredientName = getIngredientNameById(ingredientId);
                double availableQty = currentStock != null ? currentStock.getQuantity() : 0.0;
                throw new RuntimeException("Stock insuffisant pour l'ingrédient '" + ingredientName +
                        "'. Requis: " + String.format("%.2f", requiredQty) +
                        ", Disponible: " + String.format("%.2f", availableQty));
            }
        }


        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);

            String orderSql = """
            INSERT INTO "order" (reference, creation_datetime)
            VALUES (?, ?)
            RETURNING id
            """;

            int generatedOrderId;
            try (PreparedStatement ps = conn.prepareStatement(orderSql)) {
                ps.setString(1, orderToSave.getReference());
                ps.setObject(2, orderToSave.getCreationDateTime(), Types.TIMESTAMP);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Échec de la création de la commande, aucun ID retourné");
                    }
                    generatedOrderId = rs.getInt("id");
                }
            }

            String dishOrderSql = """
            INSERT INTO dish_order (id_order, id_dish, quantity)
            VALUES (?, ?, ?)
            RETURNING id
            """;

            String stockOutSql = """
            INSERT INTO stock_movement 
                (id_ingredient, quantity, unit, movement_type, creation_datetime)
            VALUES (?, ?, ?, 'OUT', ?)
            """;

            try (PreparedStatement psDish = conn.prepareStatement(dishOrderSql);
                 PreparedStatement psStock = conn.prepareStatement(stockOutSql)) {

                for (DishOrder doItem : orderToSave.getDishOrders()) {
                    int qty = doItem.getQuantity() != null ? doItem.getQuantity() : 0;
                    if (qty <= 0) continue;

                    psDish.setInt(1, generatedOrderId);
                    psDish.setInt(2, doItem.getDish().getId());
                    psDish.setInt(3, qty);
                    try (ResultSet rs = psDish.executeQuery()) {
                        if (rs.next()) {
                            doItem.setId(rs.getInt("id"));
                        }
                    }

                    for (DishIngredient di : doItem.getDish().getIngredients()) {
                        double qtyUsed = di.getQuantityRequired() * qty;

                        psStock.setInt(1, di.getIngredient().getId());
                        psStock.setDouble(2, qtyUsed);
                        psStock.setString(3, di.getUnit().name());
                        psStock.setObject(4, orderToSave.getCreationDateTime(), Types.TIMESTAMP);
                        psStock.addBatch();
                    }
                }
                psStock.executeBatch();
            }
            conn.commit();

            orderToSave.setId(generatedOrderId);
            return orderToSave;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {}
            }
            throw new RuntimeException("Erreur lors de la sauvegarde de la commande : " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {

                }
            }
        }
    }

    public Order findOrderByReference(String reference) {
        if (reference == null || reference.trim().isEmpty()) {
            throw new IllegalArgumentException("La référence ne peut pas être vide");
        }

        String sql = """
        SELECT o.id, o.reference, o.creation_datetime,
               do.id AS do_id, do.quantity,
               d.id AS dish_id, d.name, d.dish_type, d.price
        FROM "order" o
        LEFT JOIN dish_order do ON do.id_order = o.id
        LEFT JOIN dish d ON do.id_dish = d.id
        WHERE o.reference = ?
        ORDER BY do.id
        """;

        Order order = null;
        List<DishOrder> dishOrders = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, reference);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (order == null) {
                        order = new Order(
                                rs.getInt("id"),
                                rs.getString("reference"),
                                rs.getTimestamp("creation_datetime").toInstant(),
                                dishOrders
                        );
                    }

                    Integer doId = rs.getInt("do_id");
                    if (!rs.wasNull()) {
                        Dish dish = new Dish(
                                rs.getInt("dish_id"),
                                rs.getString("name"),
                                DishTypeEnum.valueOf(rs.getString("dish_type")),
                                rs.getDouble("price"),
                                null
                        );

                        DishOrder dishOrder = new DishOrder(
                                doId,
                                rs.getInt("quantity"),
                                dish
                        );
                        dishOrders.add(dishOrder);
                    }
                }
            }
            if (order == null) {
                throw new RuntimeException("Aucune commande trouvée avec la référence : " + reference);
            }
            return order;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la commande : " + e.getMessage(), e);
        }
    }

    public StockValue getCurrentStockValue(Integer ingredientId) {
        if (ingredientId == null) return null;

        String sql = """
        SELECT initial_quantity, initial_unit
        FROM ingredient
        WHERE id = ?
        """;

        StockValue initial = null;
        List<StockMovement> movements = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement psInit = conn.prepareStatement(sql)) {

            psInit.setInt(1, ingredientId);
            try (ResultSet rs = psInit.executeQuery()) {
                if (rs.next()) {
                    double qty = rs.getDouble("initial_quantity");
                    if (!rs.wasNull()) {
                        String unitStr = rs.getString("initial_unit");
                        initial = new StockValue(qty, UnitTypeEnum.valueOf(unitStr));
                    }
                }
            }

            String movSql = """
            SELECT quantity, unit, movement_type, creation_datetime
            FROM stock_movement
            WHERE id_ingredient = ?
            ORDER BY creation_datetime ASC
            """;

            try (PreparedStatement psMov = conn.prepareStatement(movSql)) {
                psMov.setInt(1, ingredientId);
                try (ResultSet rs = psMov.executeQuery()) {
                    while (rs.next()) {
                        StockValue val = new StockValue(
                                rs.getDouble("quantity"),
                                UnitTypeEnum.valueOf(rs.getString("unit"))
                        );
                        MovementTypeEnum type = MovementTypeEnum.valueOf(rs.getString("movement_type"));
                        Instant dt = rs.getTimestamp("creation_datetime").toInstant();

                        StockMovement m = new StockMovement(null, null, val.getQuantity(), val.getUnit(), type, dt);
                        movements.add(m);
                    }
                }
            }

            double total = (initial != null) ? initial.getQuantity() : 0.0;
            UnitTypeEnum unit = (initial != null) ? initial.getUnit() : null;

            for (StockMovement m : movements) {
                if (unit == null) {
                    unit = m.getValue().getUnit();
                } else if (unit != m.getValue().getUnit()) {
                    throw new IllegalStateException("Incohérence d'unité pour ingrédient id=" + ingredientId);
                }
                total += (m.getType() == MovementTypeEnum.IN) ? m.getValue().getQuantity() : -m.getValue().getQuantity();
            }

            if (unit == null) {
                throw new IllegalStateException("Aucun stock initial ni mouvement pour l'ingrédient id=" + ingredientId);
            }
            return new StockValue(total, unit);

        } catch (SQLException | IllegalArgumentException e) {
            System.err.println("Erreur lecture stock ingrédient " + ingredientId + " : " + e.getMessage());
            return null;
        }
    }

    private String getIngredientNameById(Integer ingredientId) {
        String sql = "SELECT name FROM ingredient WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du nom de l'ingrédient : " + e.getMessage());
        }
        return "Ingrédient #" + ingredientId;
    }
}