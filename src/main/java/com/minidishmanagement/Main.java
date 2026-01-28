package com.minidishmanagement;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();

        try {
            System.out.println("*** INITIALISATION DES INGRÉDIENTS ***\n");

            Ingredient laitue   = createOrUpdateIngredient(dr, 1, "Laitue",   CategoryEnum.VEGETABLE, 0.8,  5.0, UnitTypeEnum.KG);
            Ingredient tomate   = createOrUpdateIngredient(dr, 2, "Tomate",   CategoryEnum.VEGETABLE, 1.2,  4.0, UnitTypeEnum.KG);
            Ingredient poulet   = createOrUpdateIngredient(dr, 3, "Poulet",   CategoryEnum.ANIMAL,    4.5, 10.0, UnitTypeEnum.KG);
            Ingredient chocolat = createOrUpdateIngredient(dr, 4, "Chocolat", CategoryEnum.OTHER,     12.0,  3.0, UnitTypeEnum.KG);
            Ingredient beurre    = createOrUpdateIngredient(dr, 5, "Beurre",   CategoryEnum.DAIRY,     6.0,  2.5, UnitTypeEnum.KG);

            List<Ingredient> ingredients = List.of(laitue, tomate, poulet, chocolat, beurre);

            System.out.println("\n*** STOCK INITIAL ***\n");
            afficherStockInitial(ingredients);
//            System.out.println("\n*** PASSATION D'UNE COMMANDE ***\n");

            System.out.println("\n*** STOCK FINAL ***\n");
            afficherStockRecent(dr, ingredients);


            System.out.println("\nMouvements récents du poulet :");
            afficherMouvements(dr, poulet.getId(), poulet.getName());

        } catch (Exception e) {
            System.err.println("ERREUR GLOBALE : " + e.getMessage());
        }
    }
    private static Ingredient createOrUpdateIngredient(DataRetriever dr,
                                                       Integer id,
                                                       String nom,
                                                       CategoryEnum cat,
                                                       double prix,
                                                       double stockInitialKg,
                                                       UnitTypeEnum unite) {
        Ingredient ing = new Ingredient();
        ing.setId(id);
        ing.setName(nom);
        ing.setCategory(cat);
        ing.setPrice(prix);

        StockValue stockInit = new StockValue(stockInitialKg, unite);
        ing.setInitialStock(stockInit);

        dr.saveIngredient(ing);
        System.out.println("Ingrédient sauvegardé : " + nom + " (id=" + ing.getId() + ")");
        return ing;
    }

    private static void afficherStockRecent(DataRetriever dr, List<Ingredient> ingredients) {
        System.out.printf("%-12s | %-20s | %s%n", "Ingrédient", "Calcul", "Stock");
        System.out.println("-------------|---------------------|--------");

        for (Ingredient ing : ingredients) {
            double initial = (ing.getInitialStock() != null) ? ing.getInitialStock().getQuantity() : 0.0;
            double sorti = 0.0;

            List<StockMovement> mouvements = dr.findStockMovementsByIngredientId(ing.getId());
            for (StockMovement mvt : mouvements) {
                if (mvt.getType() == MovementTypeEnum.OUT) {
                    sorti += mvt.getValue().getQuantity();
                }
            }

            double stockFinal = initial - sorti;
            String calcul = String.format("%.1f - %.2f", initial, sorti);

            System.out.printf("%-12s | %-20s | %.2f%n",
                    ing.getName(),
                    calcul,
                    stockFinal);
        }
        System.out.println();
    }

    private static void afficherStockInitial(List<Ingredient> ingredients) {
        System.out.println("\nIngrédient          Stock initial");
        System.out.println("----------------------------------");

        for (Ingredient ing : ingredients) {
            StockValue init = ing.getInitialStock();
            if (init != null) {
                System.out.printf("%-18s %.1f %s%n",
                        ing.getName(),
                        init.getQuantity(),
                        init.getUnit());
            } else {
                System.out.printf("%-18s (aucun stock initial défini)%n", ing.getName());
            }
        }
        System.out.println();
    }
//    Dish saladeCesar = createSaladeCesar(dr, laitue, tomate, poulet);

//    private static Dish createSaladeCesar(DataRetriever dr, Ingredient laitue, Ingredient tomate, Ingredient poulet) {
//        Dish plat = new Dish();
//        plat.setName("Salade César");
//        plat.setDishType(DishTypeEnum.MAIN);
//        plat.setPrice(18.50);
//
//        List<DishIngredient> ingredients = new ArrayList<>();
//
//        DishIngredient diLaitue = new DishIngredient();
//        diLaitue.setIngredient(laitue);
//        diLaitue.setQuantityRequired(0.2);
//        diLaitue.setUnit(UnitTypeEnum.KG);
//        ingredients.add(diLaitue);
//
//        DishIngredient diTomate = new DishIngredient();
//        diTomate.setIngredient(tomate);
//        diTomate.setQuantityRequired(0.15);
//        diTomate.setUnit(UnitTypeEnum.KG);
//        ingredients.add(diTomate);
//
//        DishIngredient diPoulet = new DishIngredient();
//        diPoulet.setIngredient(poulet);
//        diPoulet.setQuantityRequired(1.0);
//        diPoulet.setUnit(UnitTypeEnum.KG);
//        ingredients.add(diPoulet);
//
//        plat.setIngredients(ingredients);
//
//        dr.saveDish(plat);
//        System.out.println("Plat créé : " + plat.getName() + " (id=" + plat.getId() + ")");
//
//        return dr.findDishById(plat.getId());
//    }

    private static void afficherMouvements(DataRetriever dr, int ingredientId, String nom) {
        List<StockMovement> mvt = dr.findStockMovementsByIngredientId(ingredientId);
        System.out.println("Mouvements pour " + nom + " :");
        if (mvt.isEmpty()) {
            System.out.println("-> aucun mouvement enregistré");
        } else {
            for (StockMovement m : mvt) {
                System.out.printf("  %s  %.2f %s  (%s)  %s%n",
                        m.getType(),
                        m.getValue().getQuantity(),
                        m.getValue().getUnit(),
                        m.getCreationDatetime(),
                        m.getId() != null ? "id=" + m.getId() : "");
            }
        }
        System.out.println();
    }
}