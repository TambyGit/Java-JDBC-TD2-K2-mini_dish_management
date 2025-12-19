package com.minidishmanagement;

import java.util.List;

public class Dish {
    private final int id;
    private final String name;
    private final DishTypeEnum dishTypeEnum;
    private final List<Ingredient> ingredients;

    public Dish(int id, String name, DishTypeEnum dishTypeEnum, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.dishTypeEnum = dishTypeEnum;
        this.ingredients = ingredients;
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

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public Double getDishPrice() {
        return ingredients.stream()
                .mapToDouble(Ingredient::getPrice)
                .sum();
    }


    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dishTypeEnum=" + dishTypeEnum +
                ", ingredients=" + ingredients +
                '}';
    }
}
