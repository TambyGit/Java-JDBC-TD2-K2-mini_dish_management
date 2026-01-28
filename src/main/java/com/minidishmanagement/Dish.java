package com.minidishmanagement;

import java.util.List;
import java.util.Objects;

public class Dish {
    private Integer id;
    private String name;
    private DishTypeEnum dishType;
    private Double selling_price;
    private List<DishIngredient> ingredients;

    public Dish() {

    }

    public Dish(Integer id, String name, DishTypeEnum dishType, Double selling_price, List<DishIngredient> ingredients) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.selling_price = selling_price;
        this.ingredients = ingredients;
    }

    public void setIngredients(List<DishIngredient> ingredients) {
        this.ingredients = ingredients;
        if (ingredients != null) {
            for (DishIngredient di : ingredients) {
                di.setDish(this);
            }
        }
    }

    public Double getDishCost() {
        if (ingredients == null || ingredients.isEmpty()) {
            return 0.0;
        }
        return ingredients.stream()
                .mapToDouble(dishIngredient -> {
                    Ingredient ingredient = dishIngredient.getIngredient();
                    if (ingredient == null || ingredient.getPrice() == null) {
                        return 0.0;
                    }
                    Double quantityRequired = dishIngredient.getQuantityRequired();
                    if (quantityRequired == null) {
                        return 0.0;
                    }
                    return ingredient.getPrice() * quantityRequired;
                })
                .sum();
    }

    public Double getGrossMargin() {
        if (selling_price == null) {
            return null;
        }
        Double cost = getDishCost();
        return selling_price - cost;
    }
    public List<DishIngredient> getIngredients() {
        return ingredients;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DishTypeEnum getDishType() {
        return dishType;
    }

    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public Double getPrice() {
        return selling_price;
    }

    public void setPrice(Double selling_price) {
        this.selling_price = Dish.this.selling_price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dish)) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dishType=" + dishType +
                ", selling_price=" + selling_price +
                ", ingredients=" + ingredients +
                '}';
    }
}