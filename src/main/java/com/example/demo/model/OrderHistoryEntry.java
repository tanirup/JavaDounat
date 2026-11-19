package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "order_history")
public class OrderHistoryEntry {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderedAt;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(nullable = false)
    private int totalPrice;

    @Column(nullable = false, length = 1000)
    private String itemSummaryText;

    protected OrderHistoryEntry() {
    }

    public OrderHistoryEntry(String paymentMethod, int totalPrice, List<String> itemSummary) {
        this.orderedAt = LocalDateTime.now().format(FORMATTER);
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
        this.itemSummaryText = String.join("、", itemSummary);
    }

    public Long getId() {
        return id;
    }

    public String getOrderedAt() {
        return orderedAt;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public List<String> getItemSummary() {
        if (itemSummaryText == null || itemSummaryText.isBlank()) {
            return List.of();
        }
        return Arrays.asList(itemSummaryText.split("、"));
    }
}