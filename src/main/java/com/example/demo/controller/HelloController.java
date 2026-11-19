package com.example.demo.controller;

import com.example.demo.model.CartItem;
import com.example.demo.model.Item;
import com.example.demo.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HelloController {

    @Autowired
    private ItemRepository itemRepository;

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/items")
    public String itemList(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "items"; 
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }
        model.addAttribute("cartItems", cart);

        int totalPrice = cart.stream().mapToInt(CartItem::getSubtotal).sum();
        model.addAttribute("totalPrice", totalPrice);

        return "cart"; 
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long itemId, @RequestParam int quantity, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }

        Item item = itemRepository.findById(itemId).orElse(null);
        if (item != null) {
            boolean found = false;
            for (CartItem ci : cart) {
                if (ci.getItemId().equals(itemId)) {
                    ci.setQuantity(ci.getQuantity() + quantity);
                    found = true;
                    break;
                }
            }
            if (!found) {
                cart.add(new CartItem(item.getId(), item.getName(), item.getPrice(), item.getImageUrl(), quantity));
            }
            session.setAttribute("cart", cart);
        }

        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam Long itemId, @RequestParam int quantity, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null) {
            for (CartItem ci : cart) {
                if (ci.getItemId().equals(itemId)) {
                    if (quantity > 0) {
                        ci.setQuantity(quantity);
                    }
                    break;
                }
            }
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long itemId, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null) {
            cart.removeIf(ci -> ci.getItemId().equals(itemId));
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }
    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        int totalPrice = cart.stream().mapToInt(CartItem::getSubtotal).sum();
        model.addAttribute("cartItems", cart);
        model.addAttribute("totalPrice", totalPrice);
        return "checkout";
    }

@PostMapping("/checkout/complete")
    public String completeCheckout(@RequestParam(required = false, defaultValue = "クレジットカード（疑似）") String paymentMethod,
                                   HttpSession session,
                                   Model model) {
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/items";
        }

        // 購入された数量分だけ在庫を減らしてDBに保存
        for (CartItem ci : cart) {
            itemRepository.findById(ci.getItemId()).ifPresent(item -> {
                int currentStock = (item.getStockQuantity() != null) ? item.getStockQuantity() : 0;
                int updatedStock = Math.max(0, currentStock - ci.getQuantity());
                item.setStockQuantity(updatedStock);
                itemRepository.save(item);
            });
        }

        int totalPrice = cart.stream().mapToInt(CartItem::getSubtotal).sum();
        model.addAttribute("orderedItems", new ArrayList<>(cart));
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("paymentMethod", paymentMethod);

        // 決済完了後にカートをクリア
        session.removeAttribute("cart");
        return "order-complete";
    }
}

