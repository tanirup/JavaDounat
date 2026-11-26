package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "items")
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Integer price;
    private Integer stockQuantity;
    private Integer isRecommended;
    private String imageUrl;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getPrice() { return price; }
    public Integer getStockQuantity() { return stockQuantity; }
    public Integer getIsRecommended() { return isRecommended; }
    public String getImageUrl() { return imageUrl; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
}