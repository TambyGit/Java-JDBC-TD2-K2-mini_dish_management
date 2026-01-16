package com.minidishmanagement;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever();

        System.out.println("**** TESTS DataRetriever ****\n");

        System.out.println("a) findDishById(1) :");
        try {
            Dish dish = dataRetriever.findDishById(1);
            if (dish != null) {
                System.out.println(dish);
                System.out.println("Ingrédients: " + dish.getIngredients().size());
                dish.getIngredients().forEach(System.out::println);
            } else {
                System.out.println("null");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        System.out.println();

        System.out.println("b) findDishById(999) :");
        try {
            Dish dish = dataRetriever.findDishById(999);
            System.out.println("ERREUR: Devrait lever RuntimeException, mais a retourné: " + dish);
        } catch (RuntimeException e) {
            System.out.println(" RuntimeException levée: " + e.getMessage());
        }
        System.out.println();

        System.out.println("c) findIngredients(page=2, size=2) :");
        List<Ingredient> ingredients = dataRetriever.findIngredients(2, 2);
        ingredients.forEach(System.out::println);
        System.out.println();

        System.out.println("d) findIngredients(page=3, size=5) :");
        List<Ingredient> ingredients2 = dataRetriever.findIngredients(3, 5);
        System.out.println("Taille: " + ingredients2.size() + (ingredients2.isEmpty() ? " (liste vid)" : ""));
        System.out.println();

        System.out.println("e) findDishsByIngredientName(\"eur\") :");
        List<Dish> dishes = dataRetriever.findDishsByIngredientName("eur");
        dishes.forEach(System.out::println);
        System.out.println();

        System.out.println("f) findIngredientsByCriteria(null, VEGETABLE, null, 1, 10) :");
        List<Ingredient> vegIngredients = dataRetriever.findIngredientsByCriteria(null, CategorieEnum.VEGETABLE, null, 1, 10);
        vegIngredients.forEach(System.out::println);
        System.out.println();

        System.out.println("g) findIngredientsByCriteria(\"cho\", null, \"Sal\", 1, 10) :");
        List<Ingredient> ingredients3 = dataRetriever.findIngredientsByCriteria("cho", null, "Sal", 1, 10);
        System.out.println("Taille: " + ingredients3.size() + (ingredients3.isEmpty() ? " (liste vide)" : ""));
        ingredients3.forEach(System.out::println);
        System.out.println();

        System.out.println("h) findIngredientsByCriteria(\"cho\", null, \"gâteau\", 1, 10) :");
        List<Ingredient> ingredients4 = dataRetriever.findIngredientsByCriteria("cho", null, "gâteau", 1, 10);
        ingredients4.forEach(System.out::println);
        System.out.println();

        System.out.println("i) createIngredients([Fromage, Oignon]) :");
        try {
            List<Ingredient> newIngredients = new ArrayList<>();
            newIngredients.add(new Ingredient(0, "Fromage", 1200.0, CategorieEnum.DAIRY, null));
            newIngredients.add(new Ingredient(0, "Oignon", 500.0, CategorieEnum.VEGETABLE, null));
            List<Ingredient> created = dataRetriever.createIngredients(newIngredients);
            System.out.println("Ingrédients créés:");
            created.forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        System.out.println();

        System.out.println("j) createIngredients([Carotte, Laitue]) - Exception attendue :");
        try {
            List<Ingredient> newIngredients = new ArrayList<>();
            newIngredients.add(new Ingredient(0, "Carotte", 2000.0, CategorieEnum.VEGETABLE, null));
            newIngredients.add(new Ingredient(0, "Laitue", 2000.0, CategorieEnum.VEGETABLE, null));
            List<Ingredient> created = dataRetriever.createIngredients(newIngredients);
            System.out.println("ERREUR: Devrait lever Exception, mais a créé: " + created.size() + " ingrédients");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        System.out.println();

        System.out.println("k) saveDish(Dish(name=Soupe de legumes, dishType=START, ingrédients=[Oignon])) :");
        try {
            List<Ingredient> oignonList = dataRetriever.findIngredientsByCriteria("Oignon", null, null, 1, 10);
            Ingredient oignon = oignonList.isEmpty() ? new Ingredient(0, "Oignon", 500.0, CategorieEnum.VEGETABLE, null) : oignonList.get(0);

            List<Ingredient> ingList = new ArrayList<>();
            ingList.add(oignon);
            Dish newDish = new Dish(0, "Soupe de legumes", DishTypeEnum.START, null, ingList);
            Dish saved = dataRetriever.saveDish(newDish);
            System.out.println("Plat créé: " + saved);
            System.out.println("Ingrédients: " + saved.getIngredients().size());
            saved.getIngredients().forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        System.out.println();

        System.out.println("l) saveDish(Dish(id=1, name=Salade fraîche, dishType=START, ingrédients=[Oignon, Laitue, Tomate, Fromage])) :");
        try {
            List<Ingredient> oignonList = dataRetriever.findIngredientsByCriteria("Oignon", null, null, 1, 10);
            List<Ingredient> fromageList = dataRetriever.findIngredientsByCriteria("Fromage", null, null, 1, 10);
            List<Ingredient> laitueList = dataRetriever.findIngredientsByCriteria("Laitue", null, null, 1, 10);
            List<Ingredient> tomateList = dataRetriever.findIngredientsByCriteria("Tomate", null, null, 1, 10);

            List<Ingredient> ingList = new ArrayList<>();
            if (!oignonList.isEmpty()) ingList.add(oignonList.get(0));
            if (!fromageList.isEmpty()) ingList.add(fromageList.get(0));
            if (!laitueList.isEmpty()) ingList.add(laitueList.get(0));
            if (!tomateList.isEmpty()) ingList.add(tomateList.get(0));

            Dish dishToUpdate = new Dish(1, "Salade fraîche", DishTypeEnum.START, null, ingList);
            Dish saved = dataRetriever.saveDish(dishToUpdate);
            System.out.println("Plat mis à jour: " + saved.getName());
            System.out.println("Ingrédients (" + saved.getIngredients().size() + "):");
            saved.getIngredients().forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        System.out.println();

        System.out.println("m) saveDish(Dish(id=1, name=Salade de fromage, dishType=START, ingrédients=[Fromage])) :");
        try {
            List<Ingredient> fromageList = dataRetriever.findIngredientsByCriteria("Fromage", null, null, 1, 10);

            List<Ingredient> ingList = new ArrayList<>();
            if (!fromageList.isEmpty()) ingList.add(fromageList.get(0));

            Dish dishToUpdate = new Dish(1, "Salade de fromage", DishTypeEnum.START, null, ingList);
            Dish saved = dataRetriever.saveDish(dishToUpdate);
            System.out.println("Plat mis à jour: " + saved.getName());
            System.out.println("Ingrédients (" + saved.getIngredients().size() + "):");
            saved.getIngredients().forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        System.out.println();

        System.out.println("**** TESTS getGrossMargin() ****\n");

        System.out.println("Test 1) findDishById(1) - Salade fraîche avec prix = 2000 :");
        try {
            Dish dish1 = dataRetriever.findDishById(1);
            System.out.println("Plat: " + dish1.getName());
            System.out.println("Prix de vente: " + dish1.getPrice());
            System.out.println("Coût des ingrédients: " + dish1.getDishCost());
            double margin = dish1.getGrossMargin();
            System.out.println("Marge brute: " + margin);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        System.out.println();

        System.out.println("Test 2) findDishById(3) - Riz aux légumes SANS prix (devrait lever RuntimeException) :");
        try {
            Dish dish3 = dataRetriever.findDishById(3);
            System.out.println("Plat: " + dish3.getName());
            System.out.println("Prix de vente: " + dish3.getPrice());
            double margin = dish3.getGrossMargin();
            System.out.println("ERREUR: Devrait lever RuntimeException, mais a retourné: " + margin);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        System.out.println();

        System.out.println("Test 3) saveDish - Créer un nouveau plat avec prix :");
        try {
            List<Ingredient> ingList = new ArrayList<>();
            List<Ingredient> oignonList = dataRetriever.findIngredientsByCriteria("Oignon", null, null, 1, 10);
            if (!oignonList.isEmpty()) {
                ingList.add(oignonList.get(0));
            }
            Dish newDish = new Dish(0, "Soupe test", DishTypeEnum.START, 1500.0, ingList);
            Dish saved = dataRetriever.saveDish(newDish);
            System.out.println("Plat créé: " + saved.getName() + " (ID: " + saved.getId() + ")");
            System.out.println("Prix de vente: " + saved.getPrice());
            System.out.println("Coût des ingrédients: " + saved.getDishCost());
            double margin = saved.getGrossMargin();
            System.out.println("Marge brute: " + margin);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        System.out.println();

        System.out.println("Test 4) saveDish - Modifier le prix du plat ID=4 (Gâteau au chocolat) :");
        try {
            Dish existingDish = dataRetriever.findDishById(4);
            System.out.println("Prix actuel: " + existingDish.getPrice());

            Dish updatedDish = new Dish(4, existingDish.getName(), existingDish.getDishTypeEnum(), 5000.0, existingDish.getIngredients());
            Dish saved = dataRetriever.saveDish(updatedDish);
            System.out.println("Prix après modification: " + saved.getPrice());
            System.out.println("Coût des ingrédients: " + saved.getDishCost());
            double margin = saved.getGrossMargin();
            System.out.println("Marge brute: " + margin);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        System.out.println();

        System.out.println("Test 5) saveDish - Mettre le prix à null pour le plat ID=4 :");
        try {
            Dish existingDish = dataRetriever.findDishById(4);
            Dish updatedDish = new Dish(4, existingDish.getName(), existingDish.getDishTypeEnum(), null, existingDish.getIngredients());
            Dish saved = dataRetriever.saveDish(updatedDish);
            System.out.println("Prix après modification: " + saved.getPrice());
            try {
                double margin = saved.getGrossMargin();
                System.out.println("ERREUR: Devrait lever RuntimeException, mais a retourné: " + margin);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        System.out.println();
    }
}