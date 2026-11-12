package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private AdminRepository adminRepository; // 追加

    @GetMapping("/admin/login")
    public String adminLogin() {
        return "admin/login";
    }

    // データベースと照合するログイン処理
    @PostMapping("/admin/login")
    public String adminAuthenticate(@RequestParam String username, 
                                    @RequestParam String password, 
                                    HttpSession session, 
                                    Model model) {
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        
        // ユーザーが存在し、かつパスワードが一致するかどうか
        if (adminOpt.isPresent() && adminOpt.get().getPassword().equals(password)) {
            session.setAttribute("adminLoggedIn", true);
            return "redirect:/admin/dashboard";
        } else {
            model.addAttribute("error", "ユーザー名またはパスワードが間違っています。");
            return "admin/login";
        }
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (session.getAttribute("adminLoggedIn") == null) {
            return "redirect:/admin/login";
        }

        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "admin/dashboard";
    }

    @GetMapping("/admin/logout")
    public String adminLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
}