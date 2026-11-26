package com.example.demo;

import java.util.Optional;
import java.time.LocalDate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepository {
    private final JdbcTemplate jdbc;

    public AccountRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void register(String name, String email, String passwordHash) {
        jdbc.update("INSERT INTO users (name, email, password) VALUES (?, ?, ?)",
                name, email, passwordHash);
    }

    public Optional<Account> findByEmail(String email) {
        return jdbc.query("SELECT email, password, role, status FROM users WHERE email = ?",
                (rs, row) -> new Account(rs.getString("email"), rs.getString("password"),
                        rs.getString("role"), rs.getString("status")), email)
                .stream().findFirst();
    }

    public Optional<Profile> findProfile(String email) {
        return jdbc.query("SELECT name, email, birthday, tel_no, avatar_data IS NOT NULL AS has_avatar, "
                        + "avatar_version FROM users WHERE email = ?",
                (rs, row) -> new Profile(rs.getString("name"), rs.getString("email"),
                        rs.getObject("birthday", LocalDate.class), rs.getString("tel_no"),
                        rs.getBoolean("has_avatar"), rs.getLong("avatar_version")), email)
                .stream().findFirst();
    }

    public Optional<byte[]> findAvatar(String email) {
        return jdbc.query("SELECT avatar_data FROM users WHERE email = ? AND avatar_data IS NOT NULL",
                (rs, row) -> rs.getBytes("avatar_data"), email).stream().findFirst();
    }

    public boolean updateAvatar(String email, byte[] png) {
        return jdbc.update("UPDATE users SET avatar_data = ?, avatar_version = avatar_version + 1, "
                + "updated_at = CURRENT_TIMESTAMP WHERE email = ?", png, email) == 1;
    }

    public void updateProfile(String currentEmail, ProfileForm form) {
        jdbc.update("UPDATE users SET name = ?, email = ?, birthday = ?, tel_no = ?, "
                        + "updated_at = CURRENT_TIMESTAMP WHERE email = ?",
                form.getName(), form.getEmail(), form.getBirthday(), form.getTelNo(), currentEmail);
    }

    public record Profile(String name, String email, LocalDate birthday, String telNo,
                          boolean hasAvatar, long avatarVersion) {
        public String initial() {
            return name == null || name.isBlank() ? "🍩" : name.substring(0, name.offsetByCodePoints(0, 1));
        }
    }

    public record Account(String email, String passwordHash, String role, String status) {}
}
