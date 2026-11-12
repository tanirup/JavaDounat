package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    // ログイン時に入力されたユーザー名から管理者情報を検索するメソッド
    Optional<Admin> findByUsername(String username);
}