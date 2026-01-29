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

            Dish platTest = createPlatTestUnitesMixtes(dr, laitue, tomate, poulet, chocolat, beurre);

            System.out.println("\n*** PASSATION D'UNE COMMANDE TEST ***\n");

            Order commande = new Order();
            commande.setReference("CMD-TEST-UNITS-" + System.currentTimeMillis());
            commande.setCreationDateTime(Instant.now());

            DishOrder ligne = new DishOrder();
            ligne.setDish(platTest);
            ligne.setQuantity(1);

            commande.setDishOrders(List.of(ligne));

            dr.saveOrder(commande);

            System.out.println("\n*** STOCK FINAL APRÈS LES NOUVEAUX MOUVEMENTS ***\n");
            afficherStockRecent(dr, ingredients);

            System.out.println("\nMouvements récents du poulet :");
            afficherMouvements(dr, poulet.getId(), poulet.getName());

        } catch (Exception e) {
            System.err.println("ERREUR GLOBALE : " + e.getMessage());
        }
        System.out.println("***Test de conversion d'unité***");

        double tomateOutKg = UnitConversion.convertToKg("Tomate", 5.0, UnitTypeEnum.PCS);
        System.out.println("5 PCS Tomate -> " + tomateOutKg + " KG");

        double laitueOutKg = UnitConversion.convertToKg("Laitue", 2.0, UnitTypeEnum.PCS);
        System.out.println("2 PCS Laitue -> " + laitueOutKg + " KG");
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
        System.out.printf("%-14s | %-22s | %-12s | %s%n",
                "Ingrédient", "Calcul (initial - sorti)", "Stock final", "Unité");
        System.out.println("---------------|---------------------------|--------------|--------");

        for (Ingredient ing : ingredients) {
            StockValue actuel = dr.getCurrentStockValue(ing.getId());
            double stockFinal = (actuel != null) ? actuel.getQuantity() : 0.0;
            String unite = (actuel != null) ? actuel.getUnit().name() : "?";

            double initial = (ing.getInitialStock() != null) ? ing.getInitialStock().getQuantity() : 0.0;
            double sorti = 0.0;

            List<StockMovement> mvts = dr.findStockMovementsByIngredientId(ing.getId());
            for (StockMovement m : mvts) {
                if (m.getType() == MovementTypeEnum.OUT) {
                    sorti += m.getValue().getQuantity();
                }
            }

            String calcul = String.format("%.1f - %.2f", initial, sorti);

            System.out.printf("%-14s | %-22s | %10.2f | %s%n",
                    ing.getName(), calcul, stockFinal, unite);
        }
        System.out.println();
    }

    private static void afficherStockInitial(List<Ingredient> ingredients) {
        System.out.println("\nIngrédient          Stock initial");
        System.out.println("-----------------+----------------");

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

    private static Dish createPlatTestUnitesMixtes(DataRetriever dr,
                                                   Ingredient laitue,
                                                   Ingredient tomate,
                                                   Ingredient poulet,
                                                   Ingredient chocolat,
                                                   Ingredient beurre) {
        Dish plat = new Dish();
        plat.setName("Plat Test - Unités Mixtes (Salade + Dessert)");
        plat.setDishType(DishTypeEnum.MAIN);
        plat.setPrice(22.00);  // prix ajusté ou garde 18.50 si tu veux

        List<DishIngredient> ingredients = new ArrayList<>();

        // Laitue : 2 PCS
        DishIngredient diLaitue = new DishIngredient();
        diLaitue.setIngredient(laitue);
        diLaitue.setQuantityRequired(2.0);
        diLaitue.setUnit(UnitTypeEnum.PCS);
        ingredients.add(diLaitue);

        // Tomate : 5 PCS
        DishIngredient diTomate = new DishIngredient();
        diTomate.setIngredient(tomate);
        diTomate.setQuantityRequired(5.0);
        diTomate.setUnit(UnitTypeEnum.PCS);
        ingredients.add(diTomate);

        // Poulet : 4 PCS
        DishIngredient diPoulet = new DishIngredient();
        diPoulet.setIngredient(poulet);
        diPoulet.setQuantityRequired(4.0);
        diPoulet.setUnit(UnitTypeEnum.PCS);
        ingredients.add(diPoulet);

        // Chocolat : 1 L
        DishIngredient diChocolat = new DishIngredient();
        diChocolat.setIngredient(chocolat);
        diChocolat.setQuantityRequired(1.0);
        diChocolat.setUnit(UnitTypeEnum.L);
        ingredients.add(diChocolat);

        // Beurre : 1 L
        DishIngredient diBeurre = new DishIngredient();
        diBeurre.setIngredient(beurre);
        diBeurre.setQuantityRequired(1.0);
        diBeurre.setUnit(UnitTypeEnum.L);
        ingredients.add(diBeurre);

        plat.setIngredients(ingredients);

        dr.saveDish(plat);
        System.out.println("Plat créé : " + plat.getName() + " (id=" + plat.getId() + ")");

        return dr.findDishById(plat.getId());  // ou return plat si l'id est setté
    }

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