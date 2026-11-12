package com.example.demo;

public class CartItem {
    private Long itemId;
    private String name;
    private int price;
    private String imageUrl;
    private int quantity;

    public CartItem(Long itemId, String name, int price, String imageUrl, int quantity) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    public Long getItemId() { return itemId; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    // 小計（価格 × 数量）
    public int getSubtotal() {
        return price * quantity;
    }
}