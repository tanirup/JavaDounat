package com.example.demo.controller;

import com.example.demo.model.CartItem;
import com.example.demo.model.Item;
import com.example.demo.model.OrderHistoryEntry;
import com.example.demo.model.StockOrderEntry;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.OrderHistoryRepository;
import com.example.demo.repository.StockOrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HelloController {

    private static final String CART_SESSION_KEY = "cart";
    private static final int LOW_STOCK_THRESHOLD = 10;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private StockOrderRepository stockOrderRepository;

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
        List<CartItem> cart = getOrCreateCart(session);
        model.addAttribute("cartItems", cart);
        model.addAttribute("totalPrice", calculateTotalPrice(cart));
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long itemId,
                            @RequestParam int quantity,
                            HttpSession session) {
        List<CartItem> cart = getOrCreateCart(session);

        itemRepository.findById(itemId).ifPresent(item -> {
            boolean found = false;
            for (CartItem ci : cart) {
                if (ci.getItemId().equals(itemId)) {
                    ci.setQuantity(ci.getQuantity() + quantity);
                    found = true;
                    break;
                }
            }
            if (!found) {
                cart.add(new CartItem(
                        item.getId(),
                        item.getName(),
                        item.getPrice(),
                        item.getImageUrl(),
                        quantity
                ));
            }
            session.setAttribute(CART_SESSION_KEY, cart);
        });

        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam Long itemId,
                             @RequestParam int quantity,
                             HttpSession session) {
        List<CartItem> cart = getCart(session);
        if (cart != null) {
            for (CartItem ci : cart) {
                if (ci.getItemId().equals(itemId)) {
                    if (quantity > 0) {
                        ci.setQuantity(quantity);
                    }
                    break;
                }
            }
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long itemId, HttpSession session) {
        List<CartItem> cart = getCart(session);
        if (cart != null) {
            cart.removeIf(ci -> ci.getItemId().equals(itemId));
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        List<CartItem> cart = getCart(session);
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("cartItems", cart);
        model.addAttribute("totalPrice", calculateTotalPrice(cart));
        return "checkout";
    }

    @PostMapping("/checkout/complete")
    public String completeCheckout(@RequestParam(required = false, defaultValue = "クレジットカード（疑似）") String paymentMethod,
                                   HttpSession session,
                                   Model model) {
        List<CartItem> cart = getCart(session);
        if (cart == null || cart.isEmpty()) {
            return "redirect:/items";
        }

        for (CartItem ci : cart) {
            itemRepository.findById(ci.getItemId()).ifPresent(item -> {
                int currentStock = (item.getStockQuantity() != null) ? item.getStockQuantity() : 0;
                int updatedStock = Math.max(0, currentStock - ci.getQuantity());
                item.setStockQuantity(updatedStock);
                itemRepository.save(item);
            });
        }

        int totalPrice = calculateTotalPrice(cart);
        List<String> summary = cart.stream()
                .map(ci -> ci.getName() + " × " + ci.getQuantity())
                .toList();
        orderHistoryRepository.save(new OrderHistoryEntry(paymentMethod, totalPrice, summary));

        model.addAttribute("orderedItems", new ArrayList<>(cart));
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("paymentMethod", paymentMethod);

        session.removeAttribute(CART_SESSION_KEY);
        return "order-complete";
    }

    @GetMapping("/admin/inventory")
    public String adminInventoryPage(HttpSession session, Model model) {
        if (session.getAttribute(AdminController.ADMIN_SESSION_KEY) == null) {
            session.setAttribute(AdminController.REDIRECT_AFTER_LOGIN_KEY, "/admin/inventory");
            return "redirect:/admin/login";
        }

        List<Item> items = itemRepository.findAll();
        long lowStockCount = items.stream()
                .filter(i -> i.getStockQuantity() != null && i.getStockQuantity() <= LOW_STOCK_THRESHOLD)
                .count();

        model.addAttribute("items", items);
        model.addAttribute("lowStockCount", lowStockCount);
        return "admin/inventory";
    }

    @PostMapping("/admin/inventory/order")
    public String adminOrderStock(@RequestParam Long itemId,
                                  @RequestParam int orderQuantity,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (session.getAttribute(AdminController.ADMIN_SESSION_KEY) == null) {
            session.setAttribute(AdminController.REDIRECT_AFTER_LOGIN_KEY, "/admin/inventory");
            return "redirect:/admin/login";
        }

        if (orderQuantity <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "発注数は1以上の数値を入力してください。");
            return "redirect:/admin/inventory";
        }

        itemRepository.findById(itemId).ifPresentOrElse(item -> {
            int currentStock = (item.getStockQuantity() != null) ? item.getStockQuantity() : 0;
            item.setStockQuantity(currentStock + orderQuantity);
            itemRepository.save(item);

            stockOrderRepository.save(new StockOrderEntry(
                    item.getName(),
                    orderQuantity,
                    item.getStockQuantity()
            ));

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    item.getName() + " を " + orderQuantity + " 個発注しました（現在庫: " + item.getStockQuantity() + " 個）。"
            );
        }, () -> redirectAttributes.addFlashAttribute("errorMessage", "対象の商品が見つかりませんでした。"));

        return "redirect:/admin/inventory";
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        return (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
    }

    private List<CartItem> getOrCreateCart(HttpSession session) {
        List<CartItem> cart = getCart(session);
        if (cart == null) {
            cart = new ArrayList<>();
        }
        return cart;
    }

    private int calculateTotalPrice(List<CartItem> cart) {
        return cart.stream().mapToInt(CartItem::getSubtotal).sum();
    }
}