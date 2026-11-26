package com.example.demo;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:avatar_tests")
@AutoConfigureMockMvc
class AvatarTests {
    @Autowired MockMvc mvc;
    @Autowired AccountRepository accounts;
    @Autowired JdbcTemplate jdbc;

    @BeforeEach
    void setup() {
        jdbc.update("DELETE FROM cart_items");
        jdbc.update("DELETE FROM users");
        accounts.register("太郎", "owner@example.com", "unused");
        accounts.register("花子", "other@example.com", "unused");
    }

    private MockMultipartFile png(int width, int height) throws Exception {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var graphics = image.createGraphics();
        graphics.setColor(Color.PINK);
        graphics.fillRect(0, 0, width, height);
        graphics.dispose();
        var bytes = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bytes);
        return new MockMultipartFile("avatar", "avatar.png", "image/png", bytes.toByteArray());
    }

    @Test
    void savesNormalizedPngAndDisplaysItInHeaderAndProfile() throws Exception {
        mvc.perform(multipart("/profile/avatar").file(png(600, 300))
                        .with(user("owner@example.com")).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.avatarUrl").value("/profile/avatar?v=1"));
        byte[] bytes = mvc.perform(get("/profile/avatar").with(user("owner@example.com")))
                .andExpect(status().isOk()).andExpect(content().contentType("image/png"))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andReturn().getResponse().getContentAsByteArray();
        var image = ImageIO.read(new ByteArrayInputStream(bytes));
        assertEquals(512, image.getWidth());
        assertEquals(512, image.getHeight());
        assertEquals(Color.PINK.getRGB(), image.getRGB(256, 256));
        for (String path : new String[]{"/", "/profile"}) {
            mvc.perform(get(path).with(user("owner@example.com")))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("/profile/avatar?v=1")));
        }
    }

    @Test
    void onlyOwnAvatarCanBeReadChangedOrRemoved() throws Exception {
        mvc.perform(multipart("/profile/avatar").file(png(32, 32))
                        .param("email", "other@example.com").param("userId", "2")
                        .with(user("owner@example.com")).with(csrf())).andExpect(status().isOk());
        assertTrue(accounts.findAvatar("owner@example.com").isPresent());
        assertTrue(accounts.findAvatar("other@example.com").isEmpty());
        mvc.perform(get("/profile/avatar").param("email", "owner@example.com").with(user("other@example.com")))
                .andExpect(status().isNotFound());
        mvc.perform(post("/profile/avatar/delete").param("email", "owner@example.com")
                        .with(user("other@example.com")).with(csrf())).andExpect(status().isOk());
        assertTrue(accounts.findAvatar("owner@example.com").isPresent());
    }

    @Test
    void replacementAndRemovalChangeVersionAndRestoreInitial() throws Exception {
        for (int version = 1; version <= 2; version++) {
            mvc.perform(multipart("/profile/avatar").file(png(20, 20))
                            .with(user("owner@example.com")).with(csrf()))
                    .andExpect(jsonPath("$.avatarUrl").value("/profile/avatar?v=" + version));
        }
        mvc.perform(post("/profile/avatar/delete").with(user("owner@example.com")).with(csrf()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.avatarUrl").isEmpty());
        assertFalse(accounts.findProfile("owner@example.com").orElseThrow().hasAvatar());
        assertEquals(3, accounts.findProfile("owner@example.com").orElseThrow().avatarVersion());
        mvc.perform(get("/profile/avatar").with(user("owner@example.com"))).andExpect(status().isNotFound());
    }

    @Test
    void rejectsInvalidImagesWithoutReplacingExistingAvatar() throws Exception {
        mvc.perform(multipart("/profile/avatar").file(png(20, 20))
                .with(user("owner@example.com")).with(csrf())).andExpect(status().isOk());
        for (byte[] invalid : new byte[][]{new byte[0], "<svg xmlns='http://www.w3.org/2000/svg'/>".getBytes(), new byte[]{1, 2, 3}}) {
            mvc.perform(multipart("/profile/avatar").file(new MockMultipartFile("avatar", "fake.png", "image/png", invalid))
                            .with(user("owner@example.com")).with(csrf()))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").isNotEmpty());
        }
        assertEquals(1, accounts.findProfile("owner@example.com").orElseThrow().avatarVersion());
    }

    @Test
    void rejectsExcessiveBytesAndDimensions() throws Exception {
        mvc.perform(multipart("/profile/avatar").file(new MockMultipartFile("avatar", "large.png", "image/png",
                        new byte[(int) AvatarImageService.MAX_BYTES + 1])).with(user("owner@example.com")).with(csrf()))
                .andExpect(status().isBadRequest());
        mvc.perform(multipart("/profile/avatar").file(png(5000, 1)).with(user("owner@example.com")).with(csrf()))
                .andExpect(status().isBadRequest());
        assertTrue(accounts.findAvatar("owner@example.com").isEmpty());
    }

    @Test
    void guestsAndStaleRequestsCannotChangeAvatar() throws Exception {
        mvc.perform(get("/profile/avatar")).andExpect(status().is3xxRedirection());
        mvc.perform(multipart("/profile/avatar").file(png(20, 20)).with(csrf()))
                .andExpect(status().is3xxRedirection());
        mvc.perform(multipart("/profile/avatar").file(png(20, 20))
                        .with(user("owner@example.com")).with(csrf().useInvalidToken()))
                .andExpect(status().isSeeOther()).andExpect(redirectedUrl("/profile?sessionExpired"));
        assertTrue(accounts.findAvatar("owner@example.com").isEmpty());
    }

    @Test
    void profileEditsAndEmailChangesPreserveAvatar() throws Exception {
        mvc.perform(multipart("/profile/avatar").file(png(20, 20)).with(user("owner@example.com")).with(csrf()))
                .andExpect(status().isOk());
        mvc.perform(post("/profile").with(user("owner@example.com")).with(csrf())
                        .param("name", "次郎").param("email", "new@example.com").param("birthday", "").param("telNo", ""))
                .andExpect(redirectedUrl("/login?profileUpdated"));
        assertTrue(accounts.findAvatar("new@example.com").isPresent());
        assertEquals("次郎", accounts.findProfile("new@example.com").orElseThrow().name());
    }
}
