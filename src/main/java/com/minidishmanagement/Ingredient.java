package com.minidishmanagement;

public class Ingredient {
    private final int id;
    private final String name;
    private final double price;
    private final CategorieEnum category;
    private final Dish dish;

    public Ingredient(int id, String name, double price, CategorieEnum category, Dish dish) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.dish = dish;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public CategorieEnum getCategory() {
        return category;
    }

    public Dish getDish() {
        return dish;
    }

    public String getDishName() {
        return dish.getName();
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", dishName=" + getDishName() +
                '}';
    }
}
