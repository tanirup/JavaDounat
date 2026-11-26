package com.example.demo;

import java.security.Principal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NavigationAdvice {
    private final AccountRepository accounts;

    public NavigationAdvice(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @ModelAttribute("accountProfile")
    public AccountRepository.Profile accountProfile(Principal principal) {
        return principal == null ? null : accounts.findProfile(principal.getName()).orElse(null);
    }

    @ModelAttribute("currentUser")
    public Principal currentUser(Principal principal) {
        return principal;
    }
}
