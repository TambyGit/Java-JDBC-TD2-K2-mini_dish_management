package com.minidishmanagement;

import java.time.Instant;
import java.util.Objects;

class StockMovement {
    private Integer id;
    private Ingredient ingredient;
    private StockValue value;
    private MovementTypeEnum type;
    private Instant creationDatetime;

    public StockMovement() {
    }

    public StockMovement(Integer id, Ingredient ingredient, double quantity,
                         UnitTypeEnum unit, MovementTypeEnum type, Instant creationDatetime) {
        this.id = id;
        this.ingredient = ingredient;
        this.value = new StockValue(quantity, unit);
        this.type = type;
        this.creationDatetime = creationDatetime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public StockValue getValue() {
        return value;
    }

    public void setValue(StockValue value) {
        this.value = value;
    }

    public MovementTypeEnum getType() {
        return type;
    }

    public void setType(MovementTypeEnum type) {
        this.type = type;
    }

    public Instant getCreationDatetime() {
        return creationDatetime;
    }

    public void setCreationDatetime(Instant creationDatetime) {
        this.creationDatetime = creationDatetime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StockMovement that = (StockMovement) o;
        return Objects.equals(id, that.id) && Objects.equals(ingredient, that.ingredient) && Objects.equals(value, that.value) && type == that.type && Objects.equals(creationDatetime, that.creationDatetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ingredient, value, type, creationDatetime);
    }

    @Override
    public String toString() {
        return "StockMovement{" +
                "id=" + id +
                ", ingredient=" + (ingredient != null ? ingredient.getName() : "null") +
                ", value=" + value +
                ", type=" + type +
                ", creationDatetime=" + creationDatetime +
                '}';
    }
}
