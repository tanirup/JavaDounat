package com.example.demo;

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

    // カート画面を表示
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }
        model.addAttribute("cartItems", cart);

        // 合計金額を計算
        int totalPrice = cart.stream().mapToInt(CartItem::getSubtotal).sum();
        model.addAttribute("totalPrice", totalPrice);

        return "cart"; 
    }

    // カートに商品を追加
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

    // 【新規追加】カート内の商品数量を変更
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

    // 【新規追加】カートから商品を削除
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
}