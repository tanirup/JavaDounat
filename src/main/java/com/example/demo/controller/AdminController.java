package com.example.demo.controller;

import com.example.demo.model.Item;
import com.example.demo.repository.AdminRepository;
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

import java.util.List;

@Controller
public class AdminController {

    public static final String ADMIN_SESSION_KEY = "loggedInAdmin";
    public static final String REDIRECT_AFTER_LOGIN_KEY = "redirectAfterAdminLogin";
    private static final int LOW_STOCK_THRESHOLD = 10;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired(required = false)
    private AdminRepository adminRepository;

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private StockOrderRepository stockOrderRepository;

    @GetMapping("/admin/login")
    public String adminLoginPage(HttpSession session) {
        if (session.getAttribute(ADMIN_SESSION_KEY) != null) {
            return "redirect:/admin/dashboard";
        }
        return "admin/login";
    }

    @PostMapping("/admin/login")
    public String adminLogin(@RequestParam(required = false) String username,
                             @RequestParam(required = false) String password,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        boolean isAuthenticated = (username != null && !username.isBlank())
                && (password != null && !password.isBlank());

        if (!isAuthenticated) {
            redirectAttributes.addFlashAttribute("errorMessage", "ユーザー名とパスワードを入力してください。");
            return "redirect:/admin/login";
        }

        session.setAttribute(ADMIN_SESSION_KEY, username);
        session.removeAttribute(REDIRECT_AFTER_LOGIN_KEY);

        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/logout")
    public String adminLogout(HttpSession session) {
        session.removeAttribute(ADMIN_SESSION_KEY);
        session.removeAttribute(REDIRECT_AFTER_LOGIN_KEY);
        return "redirect:/admin/login";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (session.getAttribute(ADMIN_SESSION_KEY) == null) {
            session.setAttribute(REDIRECT_AFTER_LOGIN_KEY, "/admin/dashboard");
            return "redirect:/admin/login";
        }

        List<Item> items = itemRepository.findAll();
        long lowStockCount = items.stream()
                .filter(i -> i.getStockQuantity() != null && i.getStockQuantity() <= LOW_STOCK_THRESHOLD)
                .count();

        model.addAttribute("items", items);
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("orderCount", orderHistoryRepository.count());
        model.addAttribute("stockOrderCount", stockOrderRepository.count());
        return "admin/dashboard";
    }

    @GetMapping("/admin/history")
    public String adminHistoryPage(HttpSession session, Model model) {
        if (session.getAttribute(ADMIN_SESSION_KEY) == null) {
            session.setAttribute(REDIRECT_AFTER_LOGIN_KEY, "/admin/history");
            return "redirect:/admin/login";
        }

        model.addAttribute("orderHistory", orderHistoryRepository.findAllByOrderByIdDesc());
        model.addAttribute("stockOrderHistory", stockOrderRepository.findAllByOrderByIdDesc());
        return "admin/history";
    }
    @GetMapping("/admin/orders")
    public String adminOrderHistoryPage(HttpSession session, Model model) {
        if (session.getAttribute(ADMIN_SESSION_KEY) == null) {
            session.setAttribute(REDIRECT_AFTER_LOGIN_KEY, "/admin/orders");
            return "redirect:/admin/login";
        }

        model.addAttribute("orderHistory", orderHistoryRepository.findAllByOrderByIdDesc());
        return "admin/orders";
    }

    @GetMapping("/admin/stock-orders")
    public String adminStockOrderHistoryPage(HttpSession session, Model model) {
        if (session.getAttribute(ADMIN_SESSION_KEY) == null) {
            session.setAttribute(REDIRECT_AFTER_LOGIN_KEY, "/admin/stock-orders");
            return "redirect:/admin/login";
        }

        model.addAttribute("stockOrderHistory", stockOrderRepository.findAllByOrderByIdDesc());
        return "admin/stock-orders";
    }
}