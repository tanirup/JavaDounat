package com.example.demo;

import java.util.List;

public final class CartPolicy {
    private CartPolicy() {}

    public static int limitFor(Item item) {
        return Math.max(0, Math.min(10, item.getStockQuantity() == null ? 0 : item.getStockQuantity()));
    }

    public static int quantityInCart(List<CartItem> cart, Long id) {
        if (cart == null) return 0;
        return cart.stream().filter(item -> item.getItemId().equals(id)).mapToInt(CartItem::getQuantity)
                .findFirst().orElse(0);
    }
}
