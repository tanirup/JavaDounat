package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:registration_tests")
@AutoConfigureMockMvc
class RegistrationTests {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired PasswordEncoder passwords;

    @BeforeEach
    void clearAccounts() {
        jdbc.update("DELETE FROM cart_items");
        jdbc.update("DELETE FROM users");
    }

    private MockHttpServletRequestBuilder registration(String email, String password, String confirm) {
        return post("/register").with(csrf()).param("name", "ドーナツ太郎")
                .param("email", email).param("password", password).param("passwordConfirm", confirm);
    }

    @Test
    void registersWithHashedPasswordAndNormalizesEmail() throws Exception {
        var beforeLogin = new MockHttpSession();
        beforeLogin.setAttribute("draft", "kept");
        String previousId = beforeLogin.getId();
        var result = mvc.perform(registration(" Test@Example.com ", "donutPass123", "donutPass123")
                        .session(beforeLogin).param("role", "ROLE_ADMIN"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/profile?registered"))
                .andExpect(authenticated().withUsername("test@example.com").withRoles("USER")).andReturn();
        assertNotEquals(previousId, result.getRequest().getSession(false).getId());
        assertEquals("kept", result.getRequest().getSession(false).getAttribute("draft"));
        String stored = jdbc.queryForObject("SELECT password FROM users WHERE email = 'test@example.com'", String.class);
        assertNotEquals("donutPass123", stored);
        assertTrue(passwords.matches("donutPass123", stored));
        assertEquals("ROLE_USER", jdbc.queryForObject("SELECT role FROM users", String.class));
        mvc.perform(get("/profile?registered").session((MockHttpSession) result.getRequest().getSession(false)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("会員登録が完了しました")))
                .andExpect(content().string(containsString("class=\"profile-icon\"")));
    }

    @Test
    void rejectsDuplicateEmailWithoutOverwritingAccount() throws Exception {
        mvc.perform(registration("test@example.com", "originalPass", "originalPass"));
        mvc.perform(registration("TEST@example.com", "anotherPass", "anotherPass"))
                .andExpect(status().isOk()).andExpect(view().name("signup"))
                .andExpect(model().attributeHasFieldErrors("registration", "email"))
                .andExpect(content().string(containsString("既に登録されています")));
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM users", Integer.class));
        assertTrue(passwords.matches("originalPass", jdbc.queryForObject("SELECT password FROM users", String.class)));
    }

    @Test
    void rejectsInvalidValuesAndRetainsOnlyNonSecretInputs() throws Exception {
        mvc.perform(post("/register").with(csrf()).param("name", " ").param("email", "invalid-email")
                        .param("password", "short").param("passwordConfirm", "mismatch"))
                .andExpect(status().isOk()).andExpect(view().name("signup"))
                .andExpect(model().attributeHasFieldErrors("registration", "name", "email", "password", "passwordConfirm"))
                .andExpect(content().string(containsString("value=\"invalid-email\"")))
                .andExpect(content().string(not(containsString("value=\"short\""))))
                .andExpect(content().string(not(containsString("value=\"mismatch\""))));
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM users", Integer.class));
    }

    @Test
    void rejectsPasswordsThatExceedBcryptByteLimit() throws Exception {
        String password = "あ".repeat(25);
        mvc.perform(registration("test@example.com", password, password))
                .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("registration", "password"));
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM users", Integer.class));
    }

    @Test
    void registrationAndLogoutRequireCsrfTokens() throws Exception {
        mvc.perform(post("/register").param("name", "太郎").param("email", "test@example.com")
                .param("password", "donutPass123").param("passwordConfirm", "donutPass123"))
                .andExpect(status().isSeeOther()).andExpect(redirectedUrl("/register?sessionExpired"));
        mvc.perform(post("/logout")).andExpect(status().isSeeOther()).andExpect(redirectedUrl("/?sessionExpired"));
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM users", Integer.class));
    }

    @Test
    void registeredUserCanLoginSeeLogoutOnAllPagesAndLogout() throws Exception {
        mvc.perform(registration("test@example.com", "donutPass123", "donutPass123"));
        var login = mvc.perform(post("/login").with(csrf()).param("email", "TEST@example.com")
                        .param("password", "donutPass123"))
                .andExpect(authenticated().withUsername("test@example.com"))
                .andExpect(redirectedUrl("/")).andReturn();
        var session = (MockHttpSession) login.getRequest().getSession(false);
        assertNotNull(session);
        for (String path : new String[]{"/", "/items", "/cart", "/login", "/register"}) {
            String html = mvc.perform(get(path).session(session)).andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            String nav = html.substring(html.indexOf("<nav"), html.indexOf("</nav>"));
            assertTrue(nav.contains("action=\"/logout\""));
            assertTrue(nav.contains("ログアウト"));
            assertFalse(nav.contains("href=\"/login\""));
        }
        mvc.perform(post("/logout").session(session).with(csrf()))
                .andExpect(redirectedUrl("/login?logout")).andExpect(unauthenticated());
        assertTrue(session.isInvalid());
        mvc.perform(get("/")).andExpect(content().string(containsString("href=\"/login\"")))
                .andExpect(content().string(not(containsString("action=\"/logout\""))));
    }

    @Test
    void incorrectPasswordDoesNotAuthenticate() throws Exception {
        mvc.perform(registration("test@example.com", "donutPass123", "donutPass123"));
        mvc.perform(post("/login").with(csrf()).param("email", "test@example.com").param("password", "wrongPass"))
                .andExpect(redirectedUrl("/login?error")).andExpect(unauthenticated());
    }

    @Test
    void staleFormReturnsToFreshPageWithoutRegisteringOrReplayingPost() throws Exception {
        mvc.perform(registration("test@example.com", "donutPass123", "donutPass123")
                        .with(csrf().useInvalidToken()))
                .andExpect(status().isSeeOther()).andExpect(redirectedUrl("/register?sessionExpired"));
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM users", Integer.class));
        mvc.perform(get("/register?sessionExpired"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("操作は実行されていません")))
                .andExpect(content().string(containsString("name=\"_csrf\"")));
    }

    @Test
    void expiredCartRequestsDoNotChangeCartAndReturnToCorrectPages() throws Exception {
        var session = new MockHttpSession();
        for (String action : new String[]{"add", "update", "remove"}) {
            mvc.perform(post("/cart/" + action).session(session).with(csrf().useInvalidToken())
                            .param("itemId", "1").param("quantity", "1"))
                    .andExpect(status().isSeeOther())
                    .andExpect(redirectedUrl((action.equals("add") ? "/items" : "/cart") + "?sessionExpired"));
        }
        assertNull(session.getAttribute("cart"));
    }

    @Test
    void publicPagesOpenNormallyWithoutCsrfTokens() throws Exception {
        for (String path : new String[]{"/", "/register", "/signup", "/login", "/items", "/cart"}) {
            mvc.perform(get(path)).andExpect(status().isOk());
        }
    }

    @Test
    void renderedFormsHaveCsrfTokensIncludingExistingCartForms() throws Exception {
        for (String path : new String[]{"/register", "/login"}) {
            mvc.perform(get(path)).andExpect(content().string(containsString("name=\"_csrf\"")));
        }
        var session = new MockHttpSession();
        mvc.perform(post("/cart/add").session(session).with(csrf()).param("itemId", "1").param("quantity", "1"))
                .andExpect(redirectedUrl("/cart"));
        String html = mvc.perform(get("/cart").session(session)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        for (String action : new String[]{"/cart/update", "/cart/remove"}) {
            var form = java.util.regex.Pattern.compile("<form[^>]*action=\"" + action + "\"[^>]*>.*?</form>",
                    java.util.regex.Pattern.DOTALL).matcher(html);
            assertTrue(form.find());
            assertTrue(form.group().contains("name=\"_csrf\""));
        }
    }
}
