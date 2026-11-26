package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "stock_order_history")
public class StockOrderEntry {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderedAt;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private int orderQuantity;

    @Column(nullable = false)
    private int stockAfterOrder;

    protected StockOrderEntry() {
    }

    public StockOrderEntry(String itemName, int orderQuantity, int stockAfterOrder) {
        this.orderedAt = LocalDateTime.now().format(FORMATTER);
        this.itemName = itemName;
        this.orderQuantity = orderQuantity;
        this.stockAfterOrder = stockAfterOrder;
    }

    public Long getId() {
        return id;
    }

    public String getOrderedAt() {
        return orderedAt;
    }

    public String getItemName() {
        return itemName;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public int getStockAfterOrder() {
        return stockAfterOrder;
    }
}