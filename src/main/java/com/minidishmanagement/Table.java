package com.minidishmanagement;

import java.time.Instant;
import java.util.List;

public class Table {
    private Integer id;
    private Integer number;
    private List<Order> listOrder;

    public Table(Integer id, Integer number) {
        this.id = id;
        this.number = number;
        this.listOrder = listOrder;
    }

    public Integer getId() {
        return id;
    }

    public Integer getNumber() {
        return number;
    }

    public List<Order> getListOrder() {
        return listOrder;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public void setListOrder(List<Order> listOrder) {
        this.listOrder = listOrder;
    }

    public boolean isAvailableAt(Instant dateTime, DataRetriever dataRetriever) {
        if (dataRetriever == null) {
            throw new IllegalArgumentException("DataRetriever requis pour vérifier la disponibilité");
        }
        if (dateTime == null) {
            return false;
        }
        return dataRetriever.isTableAvailable(this.number, dateTime);
    }

    @Override
    public String toString() {
        return "Table{" +
                "id=" + id +
                ", number=" + number +
                ", listOrder=" + listOrder +
                '}';
    }
}