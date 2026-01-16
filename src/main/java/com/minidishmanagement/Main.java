package com.minidishmanagement;

import java.util.List;

public class Main {

    private static final DataRetriever dataRetriever = new DataRetriever();

    public static void main(String[] args) {

        System.out.println("a) Plat ID=2 (Poulet grillé) :");
        Dish dish = dataRetriever.findDishById(2);
        System.out.println(dish);
        System.out.println();

        System.out.println("b) Ingrédients page 1 (5 par page) :");
        List<Ingredient> ingredients = dataRetriever.findIngredients(1, 5);
        ingredients.forEach(System.out::println);
        System.out.println();

        System.out.println("e) Plats contenant 'chocolat' :");
        List<Dish> dishes = dataRetriever.findDishesByIngredientName("chocolat");
        dishes.forEach(System.out::println);
        System.out.println();

        System.out.println("f) Ingrédients de catégorie ANIMAL :");
        List<Ingredient> animalIngs = dataRetriever.findIngredientsByCriteria(null, CategorieEnum.ANIMAL, null, 1, 10);
        animalIngs.forEach(System.out::println);
        System.out.println();

    }
}