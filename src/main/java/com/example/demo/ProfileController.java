package com.example.demo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileController {
    private final AccountRepository accounts;

    public ProfileController(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        var account = accounts.findProfile(principal.getName());
        if (account.isEmpty()) return "redirect:/login";
        var profile = account.get();
        model.addAttribute("profile", new ProfileForm(profile.name(), profile.email(), profile.birthday(), profile.telNo()));
        return "profile";
    }

    @PostMapping("/profile")
    public String save(@Valid @ModelAttribute("profile") ProfileForm form, BindingResult errors,
                       Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        if (errors.hasErrors()) return "profile";
        try {
            accounts.updateProfile(authentication.getName(), form);
        } catch (DuplicateKeyException exception) {
            errors.rejectValue("email", "duplicate", "このメールアドレスは既に登録されています。");
            return "profile";
        }
        // メールアドレスはログインIDなので、変更時は新しいIDでログインし直す。
        if (!authentication.getName().equals(form.getEmail())) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            return "redirect:/login?profileUpdated";
        }
        return "redirect:/profile?saved";
    }
}
