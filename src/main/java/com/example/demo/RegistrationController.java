package com.example.demo;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfAuthenticationStrategy;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {
    private final AccountRepository accounts;
    private final PasswordEncoder passwords;
    private final UserDetailsService users;

    public RegistrationController(AccountRepository accounts, PasswordEncoder passwords, UserDetailsService users) {
        this.accounts = accounts;
        this.passwords = passwords;
        this.users = users;
    }

    @GetMapping({"/register", "/signup"})
    public String signup(Model model) {
        model.addAttribute("registration", new RegistrationForm("", "", "", ""));
        return "signup";
    }

    @PostMapping({"/register", "/signup"})
    public String register(@Valid @ModelAttribute("registration") RegistrationForm form,
                           BindingResult errors, HttpServletRequest request, HttpServletResponse response) {
        if (!form.password().equals(form.passwordConfirm())) {
            errors.rejectValue("passwordConfirm", "mismatch", "パスワードが一致しません。");
        }
        if (form.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            errors.rejectValue("password", "tooLong", "パスワードはUTF-8で72バイト以内にしてください（半角英数字なら72文字）。");
        }
        if (errors.hasErrors()) return "signup";

        try {
            accounts.register(form.name(), form.email(), passwords.encode(form.password()));
        } catch (DuplicateKeyException exception) {
            errors.rejectValue("email", "duplicate", "このメールアドレスは既に登録されています。");
            return "signup";
        }
        // 作成に成功した本人だけをログインさせ、セッションIDとCSRFトークンを更新する。
        var user = users.loadUserByUsername(form.email());
        var authentication = UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
        new ChangeSessionIdAuthenticationStrategy().onAuthentication(authentication, request, response);
        new CsrfAuthenticationStrategy(new HttpSessionCsrfTokenRepository())
                .onAuthentication(authentication, request, response);
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        new HttpSessionSecurityContextRepository().saveContext(context, request, response);
        return "redirect:/profile?registered";
    }
}
