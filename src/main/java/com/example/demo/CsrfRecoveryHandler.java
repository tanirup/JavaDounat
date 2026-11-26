package com.example.demo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.CsrfException;

/** 古いフォームの操作は実行せず、新しいトークンを持つ画面へ戻す。 */
public class CsrfRecoveryHandler implements AccessDeniedHandler {
    private final AccessDeniedHandler fallback = new AccessDeniedHandlerImpl();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException exception) throws IOException, ServletException {
        if (!(exception instanceof CsrfException)) {
            fallback.handle(request, response, exception);
            return;
        }
        String path = request.getRequestURI().substring(request.getContextPath().length());
        String target = switch (path) {
            case "/register", "/signup" -> "/register";
            case "/login" -> "/login";
            case "/profile", "/profile/avatar", "/profile/avatar/delete" -> "/profile";
            case "/cart/add" -> "/items";
            case "/cart/update", "/cart/remove" -> "/cart";
            default -> "/";
        };
        // 303 により、ブラウザが失敗したPOSTを再送しないようにする。
        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
        response.setHeader("Location", request.getContextPath() + target + "?sessionExpired");
    }
}
