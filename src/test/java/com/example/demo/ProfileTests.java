package com.example.demo;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:profile_tests")
@AutoConfigureMockMvc
class ProfileTests {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired AccountRepository accounts;
    @Autowired PasswordEncoder passwords;

    @BeforeEach
    void accounts() {
        jdbc.update("DELETE FROM cart_items");
        jdbc.update("DELETE FROM users");
        accounts.register("太郎", "owner@example.com", passwords.encode("donutPass123"));
        accounts.register("花子", "other@example.com", passwords.encode("otherPass123"));
    }

    private MockHttpServletRequestBuilder save(String name, String email, String birthday, String phone) {
        return post("/profile").with(user("owner@example.com")).with(csrf())
                .param("name", name).param("email", email).param("birthday", birthday).param("telNo", phone);
    }

    @Test
    void guestsAreSentToLoginAndDoNotSeeProfileIcon() throws Exception {
        mvc.perform(get("/profile")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        mvc.perform(get("/")).andExpect(content().string(not(containsString("class=\"profile-icon\""))));
        mvc.perform(post("/profile").with(csrf()).param("name", "不正更新"))
                .andExpect(status().is3xxRedirection());
        assertEquals("太郎", accounts.findProfile("owner@example.com").orElseThrow().name());
    }

    @Test
    void loggedInUserSeesOwnIconAndProfileOnAllPages() throws Exception {
        for (String path : new String[]{"/", "/items", "/cart", "/login", "/register", "/profile"}) {
            mvc.perform(get(path).with(user("owner@example.com")))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("class=\"profile-icon\"")))
                    .andExpect(content().string(containsString("太郎さんのプロフィールを開く")))
                    .andExpect(content().string(not(containsString("花子さん"))));
        }
        mvc.perform(get("/profile").with(user("owner@example.com")))
                .andExpect(content().string(containsString("value=\"owner@example.com\"")))
                .andExpect(content().string(containsString("name=\"_csrf\"")));
    }

    @Test
    void savesOnlyAuthenticatedUsersProfileAndUpdatesInitial() throws Exception {
        mvc.perform(save("次郎", "owner@example.com", "2000-01-02", "090-1234-5678")
                        .param("userId", "2").param("currentEmail", "other@example.com"))
                .andExpect(redirectedUrl("/profile?saved"));
        var own = accounts.findProfile("owner@example.com").orElseThrow();
        assertEquals("次郎", own.name());
        assertEquals("次", own.initial());
        assertEquals(LocalDate.of(2000, 1, 2), own.birthday());
        assertEquals("090-1234-5678", own.telNo());
        assertEquals("花子", accounts.findProfile("other@example.com").orElseThrow().name());
        mvc.perform(get("/profile?saved").with(user("owner@example.com")))
                .andExpect(content().string(containsString("プロフィールを保存しました")))
                .andExpect(content().string(containsString("次郎さんのプロフィールを開く")));
    }

    @Test
    void duplicateEmailDoesNotPartiallyUpdateProfile() throws Exception {
        mvc.perform(save("変更されない", "OTHER@example.com", "2000-01-02", "09012345678"))
                .andExpect(status().isOk()).andExpect(view().name("profile"))
                .andExpect(model().attributeHasFieldErrors("profile", "email"));
        assertEquals("太郎", accounts.findProfile("owner@example.com").orElseThrow().name());
        assertEquals("花子", accounts.findProfile("other@example.com").orElseThrow().name());
    }

    @Test
    void invalidProfileAndInvalidDateRemainOnFormWithoutSaving() throws Exception {
        mvc.perform(save(" ", "invalid", "2999-01-01", "abc"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("profile", "name", "email", "birthday", "telNo"));
        mvc.perform(save("太郎", "owner@example.com", "not-a-date", ""))
                .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("profile", "birthday"));
        assertNull(accounts.findProfile("owner@example.com").orElseThrow().birthday());
    }

    @Test
    void emailChangeRequiresLoginWithNewEmail() throws Exception {
        mvc.perform(save("太郎", " New@Example.com ", "", ""))
                .andExpect(redirectedUrl("/login?profileUpdated")).andExpect(unauthenticated());
        assertTrue(accounts.findProfile("owner@example.com").isEmpty());
        assertTrue(accounts.findProfile("new@example.com").isPresent());
        mvc.perform(post("/login").with(csrf()).param("email", "new@example.com").param("password", "donutPass123"))
                .andExpect(authenticated().withUsername("new@example.com"));
        mvc.perform(post("/login").with(csrf()).param("email", "owner@example.com").param("password", "donutPass123"))
                .andExpect(unauthenticated());
    }

    @Test
    void staleProfilePostIsNotSavedAndReturnsToProfile() throws Exception {
        mvc.perform(save("変更されない", "owner@example.com", "", "").with(csrf().useInvalidToken()))
                .andExpect(status().isSeeOther()).andExpect(redirectedUrl("/profile?sessionExpired"));
        assertEquals("太郎", accounts.findProfile("owner@example.com").orElseThrow().name());
    }
}
