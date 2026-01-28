package com.minidishmanagement;

import java.time.Instant;
import java.util.List;

public class Order {
    private Integer id;
    private String reference;
    private Instant creationDateTime;
    private List<DishOrder> dishOrders;

    public Order(Integer id, String reference, Instant creationDateTime, List<DishOrder> dishOrders) {
        this.id = id;
        this.reference = reference;
        this.creationDateTime = creationDateTime;
        this.dishOrders = dishOrders;
    }

    public Order() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Instant getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(Instant creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public List<DishOrder> getDishOrders() {
        return dishOrders;
    }

    public void setDishOrders(List<DishOrder> dishOrders) {
        this.dishOrders = dishOrders;
    }

    public Double getTotalAmountWithoutVAT() {
        if (dishOrders == null) return 0.0;
        return dishOrders.stream()
                .mapToDouble(dishOrder -> dishOrder.getDish().getPrice() * dishOrder.getQuantity())
                .sum();
    }
    public Double getTotalAmountWithVAT(){
        return getTotalAmountWithoutVAT()*1.2;
    }
}
