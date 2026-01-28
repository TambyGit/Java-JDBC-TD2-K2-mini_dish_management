package com.minidishmanagement;

public class DishOrder {
    private Integer id;
    private Integer quantity;
    private Dish dish;

    public DishOrder(Integer id, Integer quantity, Dish dish) {
        this.id = id;
        this.quantity = quantity;
        this.dish = dish;
    }

    public DishOrder() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    @Override
    public String toString() {
        return "DishOrder{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", dish=" + dish +
                '}';
    }
}
