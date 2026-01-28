package com.minidishmanagement;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Ingredient {
    private Integer id;
    private String name;
    private CategoryEnum category;
    private Double price;
    private StockValue initialStock;
    private List<StockMovement> stockMovementList;

    public Ingredient(Integer id, String name, CategoryEnum category, Double price, List<StockMovement> stockMovementList, StockValue stockValue) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockMovementList = stockMovementList;
        this.initialStock = stockValue;
    }
    public Ingredient() {

    }

    public Ingredient(int ingredientId, String ingredientName, CategoryEnum ingredientCategory, double ingredientPrice) {
    }

    public Integer getId() {

        return id;
    }

    public String getName()
    {
        return name;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public Double getPrice() {
        return price;
    }

    public List<StockMovement> getStockMovementList() {
        return stockMovementList;
    }

    public StockValue getInitialStock() {
        return initialStock;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setStockMovementList(List<StockMovement> stockMovementList) {
        this.stockMovementList = stockMovementList;
    }

    public void setInitialStock(StockValue initialStock) {
        this.initialStock = initialStock;
    }

    public StockValue getStockValueAt(Instant t) {
        if (t == null) {
            throw new IllegalArgumentException("Instant t cannot be null");
        }
        if (stockMovementList == null || stockMovementList.isEmpty()) {
            return initialStock != null ? initialStock : new StockValue(0.0, UnitTypeEnum.KG);
        }

        double total = initialStock != null ? initialStock.getQuantity() : 0.0;
        UnitTypeEnum unit = initialStock != null ? initialStock.getUnit() : UnitTypeEnum.KG;

        List<StockMovement> sortedMovements = stockMovementList.stream()
                .filter(m -> m.getCreationDatetime() != null && !m.getCreationDatetime().isAfter(t))
                .sorted(Comparator.comparing(StockMovement::getCreationDatetime))
                .toList();

        for (StockMovement m : sortedMovements) {
            StockValue v = m.getValue();
            if (v == null || v.getUnit() == null) continue;

            if (unit != v.getUnit()) {
                throw new IllegalStateException("Incohérence d'unité pour " + name + " : "
                        + unit + " vs " + v.getUnit());
            }
            total += (m.getType() == MovementTypeEnum.IN) ? v.getQuantity() : -v.getQuantity();
        }

        return new StockValue(total, unit);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && category == that.category && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, category, price);
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", price=" + price +
                '}';
    }


}