package com.minidishmanagement;

import java.util.Collections;
import java.util.List;

public class Dish {
    private final int id;
    private final String name;
    private final DishTypeEnum dishTypeEnum;
    private final Double price;
    private final List<Ingredient> ingredients;

    public Dish(int id, String name, DishTypeEnum dishTypeEnum, Double price, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.dishTypeEnum = dishTypeEnum;
        this.price = price;
        this.ingredients = ingredients != null ? Collections.unmodifiableList(ingredients) : List.of();
    }

    public Dish(int id, String name, DishTypeEnum dishTypeEnum, List<Ingredient> ingredients) {
        this(id, name, dishTypeEnum, null, ingredients);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public DishTypeEnum getDishTypeEnum() {
        return dishTypeEnum;
    }

    public Double getPrice() {
        return price;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public double getDishCost() {
        return ingredients.stream()
                .mapToDouble(Ingredient::getPrice)
                .sum();
    }

    public double getGrossMargin() {
        if (price == null) {
            throw new IllegalStateException("Impossible de calculer la marge brute : le prix de vente n'a pas été défini.");
        }
        return price - getDishCost();
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dishTypeEnum=" + dishTypeEnum +
                ", price=" + price +
                ", ingredients=" + ingredients +
                '}';
    }
}