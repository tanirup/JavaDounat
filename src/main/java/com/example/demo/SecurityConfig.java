package com.example.demo;

import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(AccountRepository accounts) {
        return email -> {
            var account = accounts.findByEmail(email.strip().toLowerCase(Locale.ROOT))
                    .orElseThrow(() -> new UsernameNotFoundException("アカウントが見つかりません。"));
            return User.withUsername(account.email()).password(account.passwordHash())
                    .authorities(account.role()).disabled(!"ACTIVE".equals(account.status())).build();
        };
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth.requestMatchers("/profile", "/profile/**").authenticated()
                        .anyRequest().permitAll())
                .exceptionHandling(errors -> errors.accessDeniedHandler(new CsrfRecoveryHandler()))
                .formLogin(login -> login.loginPage("/login").usernameParameter("email")
                        .defaultSuccessUrl("/", true).failureUrl("/login?error").permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true).deleteCookies("JSESSIONID"))
                .build();
    }
}
