package com.example.demo;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ItemDetailController {
    private final ItemRepository items;
    private final AllergenInformationService allergens;

    public ItemDetailController(ItemRepository items, AllergenInformationService allergens) {
        this.items = items;
        this.allergens = allergens;
    }

    @GetMapping("/items/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        var item = items.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        @SuppressWarnings("unchecked")
        var cart = (List<CartItem>) session.getAttribute("cart");
        int alreadyInCart = CartPolicy.quantityInCart(cart, id);
        model.addAttribute("item", item);
        model.addAttribute("allergens", allergens.forItem(item));
        model.addAttribute("alreadyInCart", alreadyInCart);
        model.addAttribute("limit", CartPolicy.limitFor(item));
        model.addAttribute("maxQuantity", Math.max(0, CartPolicy.limitFor(item) - alreadyInCart));
        return "item-detail";
    }
}
