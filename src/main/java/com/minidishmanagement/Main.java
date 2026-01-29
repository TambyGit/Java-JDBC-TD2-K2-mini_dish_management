package com.minidishmanagement;

import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever();

        testTableAvailability(dataRetriever);
    }

    private static void testTableAvailability(DataRetriever dataRetriever) {
        System.out.println("Vérification de disponibilité d'une table.");

        Integer tableNumber = 1;
        Integer tableNumber2 = 2;
        Integer tableNumber3 = 3;
        Instant now = Instant.now();

        Table table = dataRetriever.findTableByNumber(tableNumber2);
        if (table == null) {
            System.err.println("Table n°" + tableNumber2 + " non trouvée dans la base.");
            return;
        }
        boolean isAvailable = dataRetriever.isTableAvailable(tableNumber2, now);

        System.out.println("Table n°" + tableNumber2 + " disponible maintenant ? " + isAvailable);
        System.out.println();
    }
}