package com.minidishmanagement;

import java.time.Instant;

public class TableOrder {
    private Table table;
    private Instant arrivalDateTime;
    private Instant departureDateTime;

    public TableOrder(Table table, Instant arrivalDateTime, Instant departureDateTime) {
        this.table = table;
        this.arrivalDateTime = arrivalDateTime;
        this.departureDateTime = departureDateTime;
    }

    public TableOrder() {
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Instant getArrivalDateTime() {
        return arrivalDateTime;
    }

    public void setArrivalDateTime(Instant arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime;
    }

    public Instant getDepartureDateTime() {
        return departureDateTime;
    }

    public void setDepartureDateTime(Instant departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    @Override
    public String toString() {
        return "TableOrder{" +
                "table=" + table +
                ", arrivalDateTime=" + arrivalDateTime +
                ", departureDateTime=" + departureDateTime +
                '}';
    }
}