package com.example.demo;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:item_detail_tests")
@AutoConfigureMockMvc
class ItemDetailTests {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;

    @BeforeEach
    void resetItems() {
        jdbc.update("UPDATE items SET stock_quantity = 50, allergen_info_verified = FALSE");
        jdbc.update("DELETE FROM item_allergens");
        jdbc.update("DELETE FROM allergens");
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> cart(MockHttpSession session) {
        return (List<CartItem>) session.getAttribute("cart");
    }

    @Test
    void listLinksToDetailAndEveryProductRenders() throws Exception {
        mvc.perform(get("/items")).andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/items/1\"")))
                .andExpect(content().string(not(containsString("openModal"))));
        for (int id = 1; id <= 8; id++) {
            mvc.perform(get("/items/" + id)).andExpect(status().isOk()).andExpect(view().name("item-detail"))
                    .andExpect(model().attribute("maxQuantity", 10))
                    .andExpect(content().string(containsString("name=\"quantity\"")))
                    .andExpect(content().string(containsString("name=\"_csrf\"")))
                    .andExpect(content().string(containsString("action=\"/cart/add\"")));
        }
    }

    @Test
    void referenceAllergensAreClearlyLabelledAndSourced() throws Exception {
        mvc.perform(get("/items/1"))
                .andExpect(content().string(containsString("参考情報")))
                .andExpect(content().string(containsString("この商品の原材料を確認した情報ではありません")))
                .andExpect(content().string(containsString("小麦")))
                .andExpect(content().string(containsString("乳成分")))
                .andExpect(content().string(containsString("大豆")))
                .andExpect(content().string(containsString("https://www.misterdonut.jp/m_menu/eiyou/allergen_eng.pdf")));
    }

    @Test
    void verifiedDatabaseAllergensReplaceReferenceExamples() throws Exception {
        jdbc.update("INSERT INTO allergens(name) VALUES ('くるみ')");
        jdbc.update("INSERT INTO item_allergens(item_id, allergen_id) SELECT 1, id FROM allergens WHERE name = 'くるみ'");
        jdbc.update("UPDATE items SET allergen_info_verified = TRUE WHERE id = 1");
        mvc.perform(get("/items/1"))
                .andExpect(content().string(containsString("登録済み情報")))
                .andExpect(content().string(containsString("くるみ")))
                .andExpect(content().string(not(containsString("一般的なイーストドーナツ"))))
                .andExpect(content().string(not(containsString("allergen_eng.pdf"))));
        mvc.perform(get("/items/2")).andExpect(content().string(containsString("参考情報")));
    }

    @Test
    void selectedQuantityUsesDatabasePriceAndAddsToCart() throws Exception {
        var session = new MockHttpSession();
        mvc.perform(post("/cart/add").session(session).with(csrf()).param("itemId", "1")
                        .param("quantity", "3").param("price", "1"))
                .andExpect(redirectedUrl("/cart"));
        assertEquals(1, cart(session).size());
        assertEquals(3, cart(session).get(0).getQuantity());
        assertEquals(720, cart(session).get(0).getSubtotal());
        mvc.perform(get("/items/1").session(session)).andExpect(model().attribute("maxQuantity", 7))
                .andExpect(model().attribute("alreadyInCart", 3));
    }

    @Test
    void enforcesCumulativeCartLimitAndDoesNotOverflow() throws Exception {
        var session = new MockHttpSession();
        mvc.perform(post("/cart/add").session(session).with(csrf()).param("itemId", "1").param("quantity", "10"))
                .andExpect(redirectedUrl("/cart"));
        for (String quantity : new String[]{"1", "2147483647", "-1", "0"}) {
            mvc.perform(post("/cart/add").session(session).with(csrf()).param("itemId", "1").param("quantity", quantity))
                    .andExpect(redirectedUrl("/items/1")).andExpect(flash().attributeExists("quantityError"));
            assertEquals(10, cart(session).get(0).getQuantity());
        }
        mvc.perform(get("/items/1").session(session)).andExpect(model().attribute("maxQuantity", 0))
                .andExpect(content().string(containsString("カートの数量が上限に達しています")));
    }

    @Test
    void rejectsOutOfRangeQuantityBeforeCreatingCart() throws Exception {
        for (String quantity : new String[]{"-1", "0", "11", "2147483647"}) {
            var session = new MockHttpSession();
            mvc.perform(post("/cart/add").session(session).with(csrf()).param("itemId", "1").param("quantity", quantity))
                    .andExpect(redirectedUrl("/items/1"));
            assertNull(session.getAttribute("cart"));
        }
    }

    @Test
    void stockRestrictsQuantityAndSoldOutDisablesPurchasing() throws Exception {
        jdbc.update("UPDATE items SET stock_quantity = 2 WHERE id = 1");
        mvc.perform(get("/items/1")).andExpect(model().attribute("maxQuantity", 2));
        var session = new MockHttpSession();
        mvc.perform(post("/cart/add").session(session).with(csrf()).param("itemId", "1").param("quantity", "3"))
                .andExpect(redirectedUrl("/items/1"));
        assertNull(session.getAttribute("cart"));
        jdbc.update("UPDATE items SET stock_quantity = 0 WHERE id = 1");
        mvc.perform(get("/items/1")).andExpect(model().attribute("maxQuantity", 0))
                .andExpect(content().string(containsString("売り切れ")));
        mvc.perform(post("/cart/add").session(session).with(csrf()).param("itemId", "1").param("quantity", "1"))
                .andExpect(redirectedUrl("/items/1"));
        assertNull(session.getAttribute("cart"));
    }

    @Test
    void cartUpdateValidatesSameQuantityLimits() throws Exception {
        var session = new MockHttpSession();
        mvc.perform(post("/cart/add").session(session).with(csrf()).param("itemId", "1").param("quantity", "3"));
        for (String quantity : new String[]{"0", "-1", "11"}) {
            mvc.perform(post("/cart/update").session(session).with(csrf()).param("itemId", "1").param("quantity", quantity))
                    .andExpect(redirectedUrl("/cart")).andExpect(flash().attributeExists("cartError"));
            assertEquals(3, cart(session).get(0).getQuantity());
        }
        mvc.perform(post("/cart/update").session(session).with(csrf()).param("itemId", "1").param("quantity", "2"));
        assertEquals(2, cart(session).get(0).getQuantity());
        assertEquals(480, cart(session).get(0).getSubtotal());
    }

    @Test
    void missingItemsReturn404WithoutChangingCart() throws Exception {
        mvc.perform(get("/items/999999")).andExpect(status().isNotFound());
        var session = new MockHttpSession();
        mvc.perform(post("/cart/add").session(session).with(csrf()).param("itemId", "999999").param("quantity", "1"))
                .andExpect(status().isNotFound());
        assertNull(session.getAttribute("cart"));
    }
}
