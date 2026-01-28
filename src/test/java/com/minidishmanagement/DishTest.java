package com.minidishmanagement;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests unitaires pour la classe Dish")
class DishTest {

    @Test
    @DisplayName("Test getDishCost - Calcul du coût des ingrédients")
    void testGetDishCost() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient(1, "Tomate", 600.0, CategoryEnum.VEGETABLE, null));
        ingredients.add(new Ingredient(2, "Laitue", 800.0, CategoryEnum.VEGETABLE, null));

        Dish dish = new Dish(1, "Salade", DishTypeEnum.START, 2000.0, ingredients);

        double cost = dish.getDishCost();
        double expectedCost = 600.0 + 800.0;

        assertEquals(expectedCost, cost, 0.01, "Le coût devrait être la somme des prix des ingrédients");
    }

    @Test
    @DisplayName("Test getDishCost - Plat sans ingrédients")
    void testGetDishCostEmptyIngredients() {
        Dish dish = new Dish(1, "Plat vide", DishTypeEnum.MAIN, 1000.0, new ArrayList<>());

        double cost = dish.getDishCost();

        assertEquals(0.0, cost, 0.01, "Le coût devrait être 0 si aucun ingrédient");
    }

    @Test
    @DisplayName("Test getGrossMargin - Calcul correct avec prix défini")
    void testGetGrossMargin() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient(1, "Ingrédient", 500.0, CategoryEnum.VEGETABLE, null));

        Dish dish = new Dish(1, "Plat", DishTypeEnum.MAIN, 2000.0, ingredients);

        double margin = dish.getGrossMargin();
        double expectedMargin = 2000.0 - 500.0;

        assertEquals(expectedMargin, margin, 0.01, "La marge devrait être prix - coût");
    }

    @Test
    @DisplayName("Test getGrossMargin - Marge négative (prix < coût)")
    void testGetGrossMarginNegative() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient(1, "Ingrédient cher", 5000.0, CategoryEnum.VEGETABLE, null));

        Dish dish = new Dish(1, "Plat", DishTypeEnum.MAIN, 2000.0, ingredients);

        double margin = dish.getGrossMargin();
        double expectedMargin = 2000.0 - 5000.0;

        assertEquals(expectedMargin, margin, 0.01, "La marge peut être négative");
        assertTrue(margin < 0, "La marge devrait être négative dans ce cas");
    }

    @Test
    @DisplayName("Test getGrossMargin - Exception quand prix est null")
    void testGetGrossMarginWithNullPrice() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient(1, "Ingrédient", 500.0, CategoryEnum.VEGETABLE, null));

        Dish dish = new Dish(1, "Plat", DishTypeEnum.MAIN, null, ingredients);

        assertNull(dish.getPrice(), "Le prix devrait être null");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dish.getGrossMargin();
        }, "getGrossMargin() devrait lever RuntimeException quand le prix est null");

        assertNotNull(exception.getMessage(), "Le message d'erreur ne devrait pas être null");
        assertTrue(exception.getMessage().contains("prix") ||
                        exception.getMessage().contains("marge"),
                "Le message devrait mentionner le prix ou la marge");
    }

    @Test
    @DisplayName("Test constructeur - Création avec tous les paramètres")
    void testConstructorWithAllParams() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient(1, "Test", 100.0, CategoryEnum.VEGETABLE, null));

        Dish dish = new Dish(1, "Nom", DishTypeEnum.DESSERT, 500.0, ingredients);

        assertEquals(1, dish.getId());
        assertEquals("Nom", dish.getName());
        assertEquals(DishTypeEnum.DESSERT, dish.getDishTypeEnum());
        assertEquals(500.0, dish.getPrice());
        assertEquals(1, dish.getIngredients().size());
    }

    @Test
    @DisplayName("Test constructeur - Création sans prix")
    void testConstructorWithoutPrice() {
        List<Ingredient> ingredients = new ArrayList<>();
        Dish dish = new Dish(2, "Nom2", DishTypeEnum.MAIN, ingredients);
        assertEquals(2, dish.getId());
        assertEquals("Nom2", dish.getName());
        assertNull(dish.getPrice(), "Le prix devrait être null");
    }

    @Test
    @DisplayName("Test getters - Vérification des valeurs")
    void testGetters() {
        List<Ingredient> ingredients = new ArrayList<>();
        Dish dish = new Dish(10, "Test Dish", DishTypeEnum.START, 1500.0, ingredients);

        assertEquals(10, dish.getId());
        assertEquals("Test Dish", dish.getName());
        assertEquals(DishTypeEnum.START, dish.getDishTypeEnum());
        assertEquals(1500.0, dish.getPrice());
        assertNotNull(dish.getIngredients());
        assertTrue(dish.getIngredients().isEmpty());
    }

    @Test
    @DisplayName("Test getGrossMargin - Prix exactement égal au coût")
    void testGetGrossMarginZero() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient(1, "Ingrédient", 1000.0, CategoryEnum.VEGETABLE, null));
        Dish dish = new Dish(1, "Plat", DishTypeEnum.MAIN, 1000.0, ingredients);
        double margin = dish.getGrossMargin();
        assertEquals(0.0, margin, 0.01, "La marge devrait être 0 quand prix = coût");
    }
}
