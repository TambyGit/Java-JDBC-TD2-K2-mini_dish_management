package com.minidishmanagement;

import java.util.HashMap;
import java.util.Map;

public class UnitConversion {
    private static final Map<String, Map<UnitTypeEnum, Double>> conversionFactors = new HashMap<>();

    static {

        Map<UnitTypeEnum, Double> tomate = new HashMap<>();
        tomate.put(UnitTypeEnum.KG, 1.0);
        tomate.put(UnitTypeEnum.PCS, 0.1);

        Map<UnitTypeEnum, Double> laitue = new HashMap<>();
        laitue.put(UnitTypeEnum.KG, 1.0);
        laitue.put(UnitTypeEnum.PCS, 0.5);

        Map<UnitTypeEnum, Double> chocolat = new HashMap<>();
        chocolat.put(UnitTypeEnum.KG, 1.0);
        chocolat.put(UnitTypeEnum.PCS, 0.1);
        chocolat.put(UnitTypeEnum.L, 0.4);

        Map<UnitTypeEnum, Double> poulet = new HashMap<>();
        poulet.put(UnitTypeEnum.KG, 1.0);
        poulet.put(UnitTypeEnum.PCS, 0.125);

        Map<UnitTypeEnum, Double> beurre = new HashMap<>();
        beurre.put(UnitTypeEnum.KG, 1.0);
        beurre.put(UnitTypeEnum.PCS, 0.25);
        beurre.put(UnitTypeEnum.L, 0.2);

        conversionFactors.put("Tomate", tomate);
        conversionFactors.put("Laitue", laitue);
        conversionFactors.put("Chocolat", chocolat);
        conversionFactors.put("Poulet", poulet);
        conversionFactors.put("Beurre", beurre);
    }

    public static double convertToKg(String ingredientName, double quantity, UnitTypeEnum fromUnit) {
        Map<UnitTypeEnum, Double> factors = conversionFactors.get(ingredientName);
        if (factors == null) {
            throw new IllegalArgumentException("Aucune configuration de conversion pour " + ingredientName);
        }
        Double factor = factors.get(fromUnit);
        if (factor == null) {
            throw new IllegalArgumentException("Conversion impossible de " + fromUnit + " vers KG pour " + ingredientName);
        }
        return quantity * factor;
    }
}
