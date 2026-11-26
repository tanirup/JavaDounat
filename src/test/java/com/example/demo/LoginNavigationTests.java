package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:login_navigation")
@AutoConfigureMockMvc
class LoginNavigationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homeLinksToRenderedLoginPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/login\"")));

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().string(containsString("ログイン | DONUT CAFE")))
                .andExpect(content().string(containsString("href=\"/\"")))
                .andExpect(content().string(containsString("href=\"/style.css\"")))
                .andExpect(content().string(containsString("href=\"/login.css\"")));
    }

    @Test
    void loginLinksToSignupAndSignupLinksBack() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/register\"")));

        for (String path : new String[]{"/register", "/signup"}) {
            mockMvc.perform(get(path))
                    .andExpect(status().isOk())
                    .andExpect(view().name("signup"))
                    .andExpect(content().string(containsString("新規会員登録 | DONUT CAFE")))
                    .andExpect(content().string(containsString("href=\"/login\"")))
                    .andExpect(content().string(containsString("href=\"/\"")))
                    .andExpect(content().string(containsString("href=\"/items\"")))
                    .andExpect(content().string(containsString("href=\"/cart\"")))
                    .andExpect(content().string(containsString("href=\"/#about\"")))
                    .andExpect(content().string(containsString("href=\"/#contact\"")))
                    .andExpect(content().string(containsString("href=\"/style.css\"")))
                    .andExpect(content().string(containsString("href=\"/login.css\"")));
        }
    }

    @Test
    void sharedNavigationAndStylesAreAvailable() throws Exception {
        for (String path : new String[]{"/items", "/cart", "/style.css", "/login.css"}) {
            mockMvc.perform(get(path)).andExpect(status().isOk());
        }
        mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("id=\"about\"")))
                .andExpect(content().string(containsString("id=\"contact\"")));
    }
}
