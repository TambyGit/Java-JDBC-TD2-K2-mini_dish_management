package com.minidishmanagement;

public class DishIngredient {
    private final int id;
    private final int dishId;
    private final int ingredientId;
    private final double quantityRequired;
    private final UnitTypeEnum unitType;

    public DishIngredient(int id, int dishId, int ingredientId, double quantityRequired, UnitTypeEnum unitType) {
        this.id = id;
        this.dishId = dishId;
        this.ingredientId = ingredientId;
        this.quantityRequired = quantityRequired;
        this.unitType = unitType;
    }

    public int getId() {
        return id;
    }

    public int getDishId() {
        return dishId;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public double getQuantityRequired() {
        return quantityRequired;
    }

    public UnitTypeEnum getUnitType() {
        return unitType;
    }

    @Override
    public String toString() {
        return "DishIngredient{" +
                "id=" + id +
                ", dishId=" + dishId +
                ", ingredientId=" + ingredientId +
                ", quantityRequired=" + quantityRequired +
                ", unitType=" + unitType +
                '}';
    }
}
