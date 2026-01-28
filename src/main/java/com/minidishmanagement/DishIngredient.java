package com.minidishmanagement;

public class DishIngredient {
    private Integer id;
    private Dish dish;
    private Ingredient ingredient;
    private Double quantityRequired;
    private UnitTypeEnum unit;

    public DishIngredient(Integer id, Ingredient ingredient,Double quantityRequired, UnitTypeEnum unit) {
        this.id = id;
        this.ingredient = ingredient;
        this.quantityRequired = quantityRequired;
        this.unit = unit;
    }

    public DishIngredient() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Double getQuantityRequired() {
        return quantityRequired;
    }

    public void setQuantityRequired(Double quantityRequired) {
        this.quantityRequired = quantityRequired;
    }

    public UnitTypeEnum getUnit() {
        return unit;
    }

    public void setUnit(UnitTypeEnum unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "DishIngredient{" +
                "id=" + id +
                ", ingredient=" + ingredient.getName()  +
                ", quantityRequired=" + quantityRequired +
                ", unit=" + unit +
                '}';
    }
}